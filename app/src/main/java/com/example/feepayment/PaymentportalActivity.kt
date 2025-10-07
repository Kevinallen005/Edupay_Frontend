package com.example.feepayment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.feepayment.responses.PaySuccessResponse
import com.example.feepayment.responses.PaymentDetailsResponse
import com.example.feepayment.retrofit.retrofit
import com.example.feepayment.utils.MenuHandler
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentportalActivity : AppCompatActivity(), PaymentResultListener {

    private var amountToPay: Int = 0 // Amount in rupees to be paid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_paymentportal)

        val menuIcon = findViewById<ImageView>(R.id.menuIcon)
        MenuHandler.setupMenu(this, menuIcon)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val studentId = sharedPref.getInt("studentid", -1)
        val feename = sharedPref.getString("feename", null)

        if (studentId != -1 && !feename.isNullOrEmpty()) {
            fetchPaymentDetails(studentId, feename)
        } else {
            Toast.makeText(this, "Missing student ID or fee name", Toast.LENGTH_SHORT).show()
        }

        // Payment Button Click Listener
        findViewById<Button>(R.id.proceedtopay).setOnClickListener {
            if (amountToPay > 0) {
                startPayment(amountToPay)
            } else {
                Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show()
            }
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
                        Toast.makeText(this@PaymentportalActivity, "Data not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<PaymentDetailsResponse>, t: Throwable) {
                    Toast.makeText(this@PaymentportalActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // Start Razorpay Payment
    private fun startPayment(amount: Int) {
        val checkout = Checkout()
        checkout.setKeyID("rzp_test_RCczA6kMFrGUNf") // Replace with your Razorpay key

        try {
            val options = JSONObject()
            options.put("name", "EduPay")
            options.put("description", "Fee Payment")
            options.put("currency", "INR")
            options.put("amount", amount * 100) // Razorpay accepts amount in paise

            val prefill = JSONObject()
            prefill.put("email", "test@example.com") // Optional
            prefill.put("contact", "9999999999") // Optional

            options.put("prefill", prefill)

            checkout.open(this, options)

        } catch (e: Exception) {
            Toast.makeText(this, "Payment error: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(razorpayPaymentID: String?) {
        Log.d("PaymentPortal", "onPaymentSuccess called with ID: $razorpayPaymentID")
        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val studentId = sharedPref.getInt("studentid", -1)
        val feename = sharedPref.getString("feename", "")
        Log.d("PaymentPortal", "StudentID: $studentId, FeeName: $feename")
        retrofit.instance.updatePaymentSuccess(studentId, feename ?: "")
            .enqueue(object : Callback<PaySuccessResponse> {
                override fun onResponse(call: Call<PaySuccessResponse>, response: Response<PaySuccessResponse>) {
                    Log.d("PaymentPortal", "API Response: ${response.body()?.status}, Code: ${response.code()}")

                    if (response.isSuccessful && response.body()?.status == "success") {
                        Log.d("PaymentPortal", "Payment status updated successfully")

                        // ✅ Show your own success screen (not Razorpay’s WebView)
                        val intent = Intent(this@PaymentportalActivity, PaysuccessActivity::class.java)
                        intent.putExtra("transaction_id", razorpayPaymentID)
                        intent.putExtra("amount", amountToPay)
                        intent.putExtra("student_id", studentId)
                        intent.putExtra("feename", feename)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.e("PaymentPortal", "Update failed - Response not successful")
                        Toast.makeText(this@PaymentportalActivity, "Update failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<PaySuccessResponse>, t: Throwable) {
                    Log.e("PaymentPortal", "API Error: ${t.message}", t)
                    Toast.makeText(this@PaymentportalActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // Required method for PaymentResultListener
    override fun onPaymentError(code: Int, response: String?) {
        Toast.makeText(this, "Payment failed: $response", Toast.LENGTH_LONG).show()
    }
}
