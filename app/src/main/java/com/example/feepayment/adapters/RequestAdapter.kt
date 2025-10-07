package com.example.feepayment.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.feepayment.R
import com.example.feepayment.responses.RequestData

class RequestAdapter(private val requests: List<RequestData>) :
    RecyclerView.Adapter<RequestAdapter.RequestViewHolder>() {

    class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val busName: TextView = itemView.findViewById(R.id.busname)
        val amount: TextView = itemView.findViewById(R.id.amount)
        val statusBtn: Button = itemView.findViewById(R.id.bookbtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.requests, parent, false)
        return RequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val request = requests[position]
        holder.busName.text = request.routename
        holder.amount.text = "₹${request.amount}"

        // Update button text and color based on status
        holder.statusBtn.text = request.status.replaceFirstChar { it.uppercase() }
        when(request.status.toLowerCase()) {
            "accepted" -> {
                holder.statusBtn.setBackgroundColor(ContextCompat.getColor(holder.statusBtn.context, R.color.lightgreen))
                holder.statusBtn.setTextColor(Color.BLACK)
            }
            "pending" -> {
                holder.statusBtn.setBackgroundColor(ContextCompat.getColor(holder.statusBtn.context, R.color.LightYellow))
                holder.statusBtn.setTextColor(Color.BLACK)
            }
            "rejected" -> {
                holder.statusBtn.setBackgroundColor(ContextCompat.getColor(holder.statusBtn.context, R.color.Red))
                holder.statusBtn.setTextColor(Color.BLACK)
            }
        }
        }

    override fun getItemCount() = requests.size
}
