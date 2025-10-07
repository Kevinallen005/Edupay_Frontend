package com.example.feepayment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.feepayment.responses.PaymentDetailsResponse
import com.example.feepayment.retrofit.retrofit
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback

class PaysuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.paysuccess)



        val amount = intent.getIntExtra("amount", 0)
        val transactionId = intent.getStringExtra("transaction_id")

        Log.d("PaySuccessActivity", "Amount: $amount, TransactionID: $transactionId")

        findViewById<TextView>(R.id.paymentAmount).text = "₹$amount"
        findViewById<TextView>(R.id.transactionId).text = "Transaction ID: $transactionId"

        findViewById<Button>(R.id.backToHomeBtn).setOnClickListener {
            startActivity(Intent(this, StudenthomeActivity::class.java))
            finish()
        }
    }
}