package com.example.feepayment

import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.feepayment.responses.PaymentDetailsResponse
import com.example.feepayment.responses.SplitInstallmentResponse
import com.example.feepayment.retrofit.retrofit
import com.example.feepayment.utils.MenuHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplitmainpageActivity : AppCompatActivity() {

    private var amountToPay: Int = 0
    private var studentId: Int = -1
    private var feeName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splitmainpage)

        val menuIcon = findViewById<ImageView>(R.id.menuIcon)
        MenuHandler.setupMenu(this, menuIcon)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        studentId = sharedPref.getInt("studentid", -1)
        feeName = sharedPref.getString("feename", null)

        if (studentId != -1 && !feeName.isNullOrEmpty()) {
            fetchPaymentDetails(studentId, feeName!!)
        } else {
            Toast.makeText(this, "Missing student ID or fee name", Toast.LENGTH_SHORT).show()
        }

        // ✅ Proceed to Split button
        findViewById<Button>(R.id.proceedtosplit).setOnClickListener {
            val radioGroup = findViewById<RadioGroup>(R.id.roleGroup)
            val selectedId = radioGroup.checkedRadioButtonId

            if (selectedId == -1) {
                Toast.makeText(this, "Please select 3 Months or 6 Months", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val months = if (selectedId == R.id.threemonths) 3 else 6
            proceedToSplit(studentId, feeName!!, months)
        }
    }

    private fun fetchPaymentDetails(studentId: Int, feename: String) {
        retrofit.instance.getPaymentDetails(studentId, feename)
            .enqueue(object : Callback<PaymentDetailsResponse> {
                override fun onResponse(
                    call: Call<PaymentDetailsResponse>,
                    response: Response<PaymentDetailsResponse>
                ) {
                    if (response.isSuccessful && response.body()?.status == "success") {
                        val data = response.body()?.data
                        if (data != null) {
                            findViewById<TextView>(R.id.name).text = data.name
                            findViewById<TextView>(R.id.amount).text = "₹${data.feeamt}"
                            findViewById<TextView>(R.id.feename).text = data.feename
                            findViewById<TextView>(R.id.duedate).text = data.duedate
                            amountToPay = data.feeamt
                        }
                    } else {
                        Toast.makeText(this@SplitmainpageActivity, "Data not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<PaymentDetailsResponse>, t: Throwable) {
                    Toast.makeText(this@SplitmainpageActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // ✅ Call API for split
    private fun proceedToSplit(studentId: Int, feename: String, months: Int) {
        retrofit.instance.splitInstallment(studentId, feename, months)
            .enqueue(object : Callback<SplitInstallmentResponse> {
                override fun onResponse(
                    call: Call<SplitInstallmentResponse>,
                    response: Response<SplitInstallmentResponse>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body?.status == "success") {
                            showPopup("Successfully Splitted", body.message)
                        } else {
                            showPopup("Split Failed", body?.message ?: "Split not available")
                        }
                    } else {
                        showPopup("Error", "Server error. Please try again.")
                    }
                }

                override fun onFailure(call: Call<SplitInstallmentResponse>, t: Throwable) {
                    showPopup("Error", "API Error: ${t.message}")
                }
            })
    }

    // ✅ Popup Dialog
    // ✅ Popup Dialog that navigates back
    private fun showPopup(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { _, _ ->
                // Go back to SplitoptionsActivity after popup
                finish()
            }
            .setCancelable(false) // force user to press OK
            .show()
    }

}
