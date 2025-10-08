package com.simats.feepayment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simats.feepayment.adapters.AdminRequestAdapter
import com.simats.feepayment.responses.AdminRequestResponse
import com.simats.feepayment.retrofit.retrofit
import com.simats.feepayment.utils.MenuHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminbusrequestsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdminRequestAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.adminbusrequests)

        val menuIcon = findViewById<ImageView>(R.id.menuIcon)
        MenuHandler.setupMenu(this, menuIcon)

        recyclerView = findViewById(R.id.busreq)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchRequests()
    }

    private fun fetchRequests() {
        retrofit.instance.getBusRequests().enqueue(object : Callback<AdminRequestResponse> {
            override fun onResponse(
                call: Call<AdminRequestResponse>,
                response: Response<AdminRequestResponse>
            ) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    val data = response.body()?.data ?: emptyList()

                    adapter = AdminRequestAdapter(this@AdminbusrequestsActivity, data, object : AdminRequestAdapter.OnRequestHandledListener {
                        override fun onRequestHandled() {
                            // Refresh after 1.5 seconds
                            Handler(Looper.getMainLooper()).postDelayed({
                                fetchRequests()
                            }, 100)
                        }
                    })

                    recyclerView.adapter = adapter
                } else {
                    Toast.makeText(this@AdminbusrequestsActivity, "No requests found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AdminRequestResponse>, t: Throwable) {
                Log.e("AdminReq", "Error: ${t.message}")
                Toast.makeText(this@AdminbusrequestsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
