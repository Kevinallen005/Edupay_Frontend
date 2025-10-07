package com.example.feepayment.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.feepayment.R
import com.example.feepayment.responses.DueItem

class DueListAdapterHome(
    private val context: Context,
    private val dueList: List<DueItem>,
    private val onPayClick: (DueItem) -> Unit
) : RecyclerView.Adapter<DueListAdapterHome.DueViewHolder>() {



    class DueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dueDate: TextView = itemView.findViewById(R.id.duedate)
        val feeName: TextView = itemView.findViewById(R.id.feename)
        val amount: TextView = itemView.findViewById(R.id.amount)
        val payBtn: androidx.appcompat.widget.AppCompatButton = itemView.findViewById(R.id.paybtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DueViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.studentpayhome, parent, false)
        return DueViewHolder(view)
    }

    override fun onBindViewHolder(holder: DueViewHolder, position: Int) {
        val item = dueList[position]
        holder.dueDate.text = "Due Date: ${item.duedate}"
        holder.feeName.text = item.feename
        holder.amount.text = "₹${item.feeamt}"

        holder.payBtn.setOnClickListener {
            onPayClick(item)
        }
    }

    override fun getItemCount(): Int = dueList.size
}
