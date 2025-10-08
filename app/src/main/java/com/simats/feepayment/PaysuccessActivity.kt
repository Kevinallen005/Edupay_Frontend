package com.simats.feepayment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

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