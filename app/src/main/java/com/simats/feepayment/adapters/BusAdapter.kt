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
import com.simats.feepayment.BusconfirmbookingActivity
import com.simats.feepayment.R
import com.simats.feepayment.responses.BusData

class BusAdapter(
    private val buses: List<BusData>,
    private val onBookClick: (BusData) -> Unit
) : RecyclerView.Adapter<BusAdapter.BusViewHolder>() {

    class BusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val busName: TextView = itemView.findViewById(R.id.busname)
        val amount: TextView = itemView.findViewById(R.id.amount)
        val seats: TextView = itemView.findViewById(R.id.seats)
        val bookBtn: Button = itemView.findViewById(R.id.bookbtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.busses, parent, false)
        return BusViewHolder(view)
    }

    override fun onBindViewHolder(holder: BusViewHolder, position: Int) {
        val bus = buses[position]
        holder.busName.text = bus.routename
        holder.amount.text = "₹${bus.amount}"
        holder.seats.text="Available Seats : ${bus.seats}"
        holder.bookBtn.setOnClickListener {
            val context = holder.itemView.context
            val sharedPref = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            val studentId = sharedPref.getInt("studentid", -1)

            if (studentId != -1) {
                val intent = Intent(context, BusconfirmbookingActivity::class.java)
                intent.putExtra("routename", bus.routename)
                intent.putExtra("studentid", studentId)
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Student ID not found", Toast.LENGTH_SHORT).show()
            }

            // Also call the click callback if needed
            onBookClick(bus)
        }
    }

    override fun getItemCount() = buses.size
}
