package com.simats.feepayment.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.simats.feepayment.R
import com.simats.feepayment.responses.NotificationItem


class NotificationAdapter(
    private val notifications: List<NotificationItem>
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val notificationText: TextView = itemView.findViewById(R.id.notification)
        val container: View = itemView.findViewById(R.id.notification_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val item = notifications[position]
        holder.notificationText.text = item.message

        val context = holder.itemView.context

        // Conditional Styling
        if (item.title.equals("Fee Reminder", ignoreCase = true)) {
            holder.notificationText.setTextColor(ContextCompat.getColor(context, R.color.black))
            holder.container.setBackgroundResource(R.drawable.curved_border_red)
        } else {
            holder.notificationText.setTextColor(ContextCompat.getColor(context, R.color.black))
            holder.container.setBackgroundResource(R.drawable.curved_border_blue)
        }
    }

    override fun getItemCount() = notifications.size
}

