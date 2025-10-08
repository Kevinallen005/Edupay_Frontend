package com.simats.feepayment.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.simats.feepayment.R
import com.simats.feepayment.responses.DueItem

class SplitoptionsAdapter(
    private val context: Context,
    private val dueList: List<DueItem>,
    private val onSplitClick: (DueItem) -> Unit
) : RecyclerView.Adapter<SplitoptionsAdapter.SplitViewHolder>() {

    class SplitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dueDate: TextView = itemView.findViewById(R.id.duedate)
        val feeName: TextView = itemView.findViewById(R.id.feename)
        val amount: TextView = itemView.findViewById(R.id.amount)
        val splitBtn: Button = itemView.findViewById(R.id.splitbtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SplitViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.studentsplitbuttons, parent, false)
        return SplitViewHolder(view)
    }

    override fun onBindViewHolder(holder: SplitViewHolder, position: Int) {
        val item = dueList[position]
        holder.dueDate.text = "Due Date: ${item.duedate}"
        holder.feeName.text = item.feename
        holder.amount.text = "₹${item.feeamt}"

        holder.splitBtn.setOnClickListener {
            onSplitClick(item)
        }
    }

    override fun getItemCount(): Int = dueList.size
}
