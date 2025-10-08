package com.simats.feepayment.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.simats.feepayment.R
import com.simats.feepayment.ReceiptActivity
import com.simats.feepayment.responses.PaymentHistoryItem

class PaymentHistoryAdapter(private val historyList: List<PaymentHistoryItem>) :
    RecyclerView.Adapter<PaymentHistoryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val feename: TextView = itemView.findViewById(R.id.feename)
        val paydate: TextView = itemView.findViewById(R.id.paydate)
        val amount: TextView = itemView.findViewById(R.id.amountHis)
        val receiptBtn: Button = itemView.findViewById(R.id.receiptbtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fees_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = historyList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = historyList[position]
        holder.feename.text = item.feename
        holder.paydate.text = item.paydate
        holder.amount.text = "₹${item.feeamt}"

        holder.receiptBtn.setOnClickListener {
            val context = holder.itemView.context
            val sharedPref = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            val studentId = sharedPref.getInt("studentid", -1)

            if (studentId != -1) {
                val intent = Intent(context, ReceiptActivity::class.java)
                intent.putExtra("studentid", studentId)
                intent.putExtra("feename", item.feename)
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Student ID not found", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
