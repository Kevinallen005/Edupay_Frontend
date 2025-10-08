package com.simats.feepayment

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simats.feepayment.adapters.AdminPaymentAdapter
import com.simats.feepayment.responses.PaymentHistoryResponse
import com.simats.feepayment.retrofit.retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminpaymenthistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdminPaymentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adminpaymenthistory)

        val menuIcon = findViewById<ImageView>(R.id.menuIcon)
        AdminMenuHandler.setupMenu(this, menuIcon)

        recyclerView = findViewById(R.id.paymentrecycle)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val studentid = intent.getIntExtra("studentId", -1)
        Log.d("PaymentDebug", "Student ID from Intent: $studentid")

        if (studentid != -1) {
            fetchHistory(studentid)
        } else {
            Toast.makeText(this, "Student ID not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchHistory(studentid: Int) {
        retrofit.instance.getPaymentHistory(studentid)
            .enqueue(object : Callback<PaymentHistoryResponse> {
                override fun onResponse(
                    call: Call<PaymentHistoryResponse>,
                    response: Response<PaymentHistoryResponse>
                ) {
                    Log.d("PaymentDebug", "Raw response body: ${response.body()}")
                    Log.d("PaymentDebug", "Response success: ${response.isSuccessful}")
                    Log.d("PaymentDebug", "Response code: ${response.code()}")

                    if (response.isSuccessful && response.body()?.status == "success") {
                        val historyList = response.body()?.data ?: emptyList()
                        adapter = AdminPaymentAdapter(historyList, studentid) // pass studentId
                        recyclerView.adapter = adapter
                    } else {
                        Toast.makeText(
                            this@AdminpaymenthistoryActivity,
                            "No history found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<PaymentHistoryResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AdminpaymenthistoryActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}
