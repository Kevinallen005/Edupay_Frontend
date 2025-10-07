package com.example.feepayment.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.feepayment.AdminreceiptActivity
import com.example.feepayment.R
import com.example.feepayment.responses.PaymentHistoryItem

class AdminPaymentAdapter(
    private val historyList: List<PaymentHistoryItem>,
    private val studentId: Int // Pass from Activity
) : RecyclerView.Adapter<AdminPaymentAdapter.ViewHolder>() {

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
            val intent = Intent(context, AdminreceiptActivity::class.java)
            intent.putExtra("studentid", studentId) // use passed ID
            intent.putExtra("feename", item.feename)
            context.startActivity(intent)
        }
    }
}
