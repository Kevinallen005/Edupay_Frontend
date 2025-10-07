package com.example.feepayment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.feepayment.adapters.PaymentHistoryAdapter
import com.example.feepayment.responses.PaymentHistoryItem
import com.example.feepayment.responses.PaymentHistoryResponse
import com.example.feepayment.retrofit.retrofit
import com.example.feepayment.utils.MenuHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymenthistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PaymentHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paymenthistory)

        val menuIcon = findViewById<ImageView>(R.id.menuIcon)
        MenuHandler.setupMenu(this, menuIcon)

        recyclerView = findViewById(R.id.paymentrecycle)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val studentid = sharedPref.getInt("studentid", -1)
        Log.d("PaymentDebug", "Student ID from SharedPreferences: $studentid")

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

                    Log.d("PaymentDebug", "Student ID from Fetched: $studentid")
                    Log.d("PaymentDebug", "Raw response body: ${response.body()}")
                    Log.d("PaymentDebug", "Response success: ${response.isSuccessful}")
                    Log.d("PaymentDebug", "Response code: ${response.code()}")


                    if (response.isSuccessful && response.body()?.status == "success") {
                        val historyList = response.body()?.data ?: emptyList()
                        adapter = PaymentHistoryAdapter(historyList)
                        recyclerView.adapter = adapter
                    } else {
                        Toast.makeText(this@PaymenthistoryActivity, "No history found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<PaymentHistoryResponse>, t: Throwable) {
                    Toast.makeText(this@PaymenthistoryActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
