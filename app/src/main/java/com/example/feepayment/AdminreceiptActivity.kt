package com.example.feepayment

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.feepayment.responses.ReceiptResponse
import com.example.feepayment.retrofit.retrofit
import com.example.feepayment.utils.MenuHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminreceiptActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_adminreceipt)


        val menuIcon = findViewById<ImageView>(R.id.menuIcon)
        AdminMenuHandler.setupMenu(this, menuIcon)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val studentId = intent.getIntExtra("studentid", -1)
        val feeName = intent.getStringExtra("feename") ?: ""

        if (studentId != -1 && feeName.isNotEmpty()) {
            fetchReceipt(studentId, feeName)
        } else {
            Toast.makeText(this, "Missing data", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun fetchReceipt(studentId: Int, feeName: String) {
        retrofit.instance.getReceiptDetails(studentId, feeName)
            .enqueue(object : Callback<ReceiptResponse> {
                override fun onResponse(
                    call: Call<ReceiptResponse>,
                    response: Response<ReceiptResponse>
                )

                {
                    Log.d("ReceiptDebug", "studentid=$studentId, feename=$feeName")
                    Log.d("ReceiptDebug", "Response code: ${response.code()}")
                    Log.d("ReceiptDebug", "Is successful: ${response.isSuccessful}")
                    Log.d("ReceiptDebug", "Raw response body: ${response.body()}")
                    Log.d("ReceiptDebug", "Error body (if any): ${response.errorBody()?.string()}")


                    if (response.isSuccessful && response.body() != null) {
                        val receipt = response.body()!!
                        Log.d("ReceiptDebug", "Receipt data: $receipt")

                        findViewById<TextView>(R.id.name).text = receipt.name
                        findViewById<TextView>(R.id.class_).text = receipt.class_
                        findViewById<TextView>(R.id.sect).text = receipt.sec
                        findViewById<TextView>(R.id.amountHis).text = "₹${receipt.feeamt}"
                        findViewById<TextView>(R.id.feename).text = receipt.feename
                        findViewById<TextView>(R.id.paydate).text = receipt.paydate
                        findViewById<TextView>(R.id.duedate).text = receipt.duedate
                        findViewById<TextView>(R.id.referenceID).text = receipt.referenceid
                        findViewById<TextView>(R.id.scholarship).text = receipt.ScholarshipAmount
                    } else {
                        Toast.makeText(this@AdminreceiptActivity, "No receipt found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ReceiptResponse>, t: Throwable) {
                    Toast.makeText(this@AdminreceiptActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
