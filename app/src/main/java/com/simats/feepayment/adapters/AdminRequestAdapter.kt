package com.simats.feepayment.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.simats.feepayment.R
import com.simats.feepayment.responses.AdminRequestData
import com.simats.feepayment.responses.ImposeFeeSubmitResponse
import com.simats.feepayment.responses.StatusResponse
import com.simats.feepayment.retrofit.retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminRequestAdapter(
    private val context: Context,
    private val requests: List<AdminRequestData>,
    private val listener: OnRequestHandledListener
) : RecyclerView.Adapter<AdminRequestAdapter.RequestViewHolder>() {

    interface OnRequestHandledListener {
        fun onRequestHandled()
    }

    class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRouteName: TextView = itemView.findViewById(R.id.tvRouteName)
        val tvBoardingPoint: TextView = itemView.findViewById(R.id.tvBoardingPoint)
        val tvStudentId: TextView = itemView.findViewById(R.id.tvStudentId)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvVia: TextView = itemView.findViewById(R.id.tvVia)
        val btnAccept: Button = itemView.findViewById(R.id.btnAccept)
        val btnReject: Button = itemView.findViewById(R.id.btnReject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adminrequests, parent, false)
        return RequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val request = requests[position]

        holder.tvRouteName.text = request.routename
        holder.tvBoardingPoint.text = request.boarding_point
        holder.tvStudentId.text = "ID: ${request.studentid}"
        holder.tvAmount.text = "₹${request.amount}"
        holder.tvStatus.text = request.status
        holder.tvVia.text = "Via: ${request.via}"

        holder.btnAccept.setOnClickListener {
            retrofit.instance.updateBusRequest(request.studentid, request.routename, "accepted")
                .enqueue(object : Callback<StatusResponse> {
                    override fun onResponse(call: Call<StatusResponse>, response: Response<StatusResponse>) {
                        if (response.isSuccessful && response.body()?.success == true) {
                            Toast.makeText(context, "Accepted successfully", Toast.LENGTH_SHORT).show()

                            val studentIdsJson = "[${request.studentid}]"
                            retrofit.instance.imposeFee(
                                "BUS FEE",
                                request.amount.toString(),
                                "2026-05-31",
                                studentIdsJson
                            ).enqueue(object : Callback<ImposeFeeSubmitResponse> {
                                override fun onResponse(call: Call<ImposeFeeSubmitResponse>, response: Response<ImposeFeeSubmitResponse>) {
                                    if (response.isSuccessful && response.body()?.status == "success") {
                                        Toast.makeText(context, "Bus Fee imposed", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Failed to impose fee", Toast.LENGTH_SHORT).show()
                                    }
                                    listener.onRequestHandled()  // Trigger refresh
                                }

                                override fun onFailure(call: Call<ImposeFeeSubmitResponse>, t: Throwable) {
                                    Toast.makeText(context, "Error imposing fee: ${t.message}", Toast.LENGTH_SHORT).show()
                                    listener.onRequestHandled()
                                }
                            })
                        } else {
                            Toast.makeText(context, "Error accepting", Toast.LENGTH_SHORT).show()
                            listener.onRequestHandled()
                        }
                    }

                    override fun onFailure(call: Call<StatusResponse>, t: Throwable) {
                        Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        listener.onRequestHandled()
                    }
                })
        }

        holder.btnReject.setOnClickListener {
            retrofit.instance.updateBusRequest(request.studentid, request.routename, "rejected")
                .enqueue(object : Callback<StatusResponse> {
                    override fun onResponse(call: Call<StatusResponse>, response: Response<StatusResponse>) {
                        if (response.isSuccessful && response.body()?.success == true) {
                            Toast.makeText(context, "Rejected successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Error rejecting request", Toast.LENGTH_SHORT).show()
                        }
                        listener.onRequestHandled()
                    }

                    override fun onFailure(call: Call<StatusResponse>, t: Throwable) {
                        Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        listener.onRequestHandled()
                    }
                })
        }
    }

    override fun getItemCount(): Int = requests.size
}
