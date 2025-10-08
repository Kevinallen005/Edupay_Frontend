package com.simats.feepayment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simats.feepayment.adapters.BusAdapter
import com.simats.feepayment.adapters.RequestAdapter
import com.simats.feepayment.responses.BusResponse
import com.simats.feepayment.responses.RequestResponse
import com.simats.feepayment.retrofit.retrofit
import com.simats.feepayment.utils.MenuHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudentbuspassActivity : AppCompatActivity() {

    private lateinit var busRecycler: RecyclerView
    private lateinit var requestRecycler: RecyclerView
    private lateinit var roleGroup: RadioGroup
    private lateinit var noRequestsText: TextView
    private lateinit var noRoutesText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_studentbuspass)

        // Menu
        val menuIcon = findViewById<ImageView>(R.id.menuIcon)
        MenuHandler.setupMenu(this, menuIcon)

        // RecyclerViews
        busRecycler = findViewById(R.id.busrecycler)
        requestRecycler = findViewById(R.id.requestrecycler)
        busRecycler.layoutManager = LinearLayoutManager(this)
        requestRecycler.layoutManager = LinearLayoutManager(this)

        // Empty state TextViews
        noRequestsText = findViewById(R.id.noRequestsText)
        noRoutesText = findViewById(R.id.noRoutesText)

        // RadioGroup
        roleGroup = findViewById(R.id.roleGroup)

        // Default = Routes
        roleGroup.check(R.id.busroutes)
        busRecycler.visibility = View.VISIBLE
        requestRecycler.visibility = View.GONE
        noRequestsText.visibility = View.GONE
        loadRoutes()

        // Switch between Routes / Requests
        roleGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.busroutes -> {
                    busRecycler.visibility = View.VISIBLE
                    requestRecycler.visibility = View.GONE
                    noRequestsText.visibility = View.GONE
                    loadRoutes()
                }
                R.id.yourrequests -> {
                    busRecycler.visibility = View.GONE
                    requestRecycler.visibility = View.VISIBLE
                    loadRequests()
                }
            }
        }
    }

    /** Fetch Bus Routes */
    private fun loadRoutes() {
        retrofit.instance.getBuses().enqueue(object : Callback<BusResponse> {
            override fun onResponse(call: Call<BusResponse>, response: Response<BusResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val busList = response.body()!!.data
                    if (busList.isNotEmpty()) {
                        noRoutesText.visibility = View.GONE
                        busRecycler.visibility = View.VISIBLE
                        busRecycler.adapter = BusAdapter(busList) { bus ->

                        }
                    } else {
                        busRecycler.visibility = View.GONE
                        noRoutesText.visibility = View.VISIBLE
                    }
                } else {
                    busRecycler.visibility = View.GONE
                    noRoutesText.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<BusResponse>, t: Throwable) {
                Log.e("BusDebug", "Error: ${t.message}")
                busRecycler.visibility = View.GONE
                noRoutesText.visibility = View.VISIBLE
                Toast.makeText(this@StudentbuspassActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /** Fetch Requests */
    private fun loadRequests() {
        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val studentId = sharedPref.getInt("studentid", -1)

        if (studentId == -1) {
            Toast.makeText(this, "Student ID not found", Toast.LENGTH_SHORT).show()
            return
        }

        retrofit.instance.getRequests(studentId).enqueue(object : Callback<RequestResponse> {
            override fun onResponse(call: Call<RequestResponse>, response: Response<RequestResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.status == "success" && body.data != null) {
                        noRequestsText.visibility = View.GONE
                        requestRecycler.visibility = View.VISIBLE
                        requestRecycler.adapter = RequestAdapter(body.data)
                    } else if (body.status == "empty") {
                        requestRecycler.visibility = View.GONE
                        noRequestsText.visibility = View.VISIBLE
                        noRequestsText.text = body.message ?: "No Requests Found"
                    }
                } else {
                    requestRecycler.visibility = View.GONE
                    noRequestsText.visibility = View.VISIBLE
                    noRequestsText.text = "No Requests Found"
                }
            }

            override fun onFailure(call: Call<RequestResponse>, t: Throwable) {
                Log.e("RequestDebug", "Error: ${t.message}")
                requestRecycler.visibility = View.GONE
                noRequestsText.visibility = View.VISIBLE
                noRequestsText.text = "Error loading requests"
                Toast.makeText(this@StudentbuspassActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
