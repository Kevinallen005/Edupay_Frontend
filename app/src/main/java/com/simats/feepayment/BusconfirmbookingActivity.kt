package com.simats.feepayment

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.simats.feepayment.responses.BusConfirmResponse
import com.simats.feepayment.responses.BusData
import com.simats.feepayment.responses.BusResponse
import com.simats.feepayment.retrofit.retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BusconfirmbookingActivity : AppCompatActivity() {

    private lateinit var tvRouteName: TextView
    private lateinit var tvKm: TextView
    private lateinit var tvAmount: TextView
    private lateinit var spinnerVia: Spinner
    private lateinit var btnConfirmBook: Button

    private var studentId: Int = -1
    private var routeName: String? = null
    private var busData: BusData? = null
    private var selectedBoarding: String? = null   // <-- selected boarding point

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.busconfirmbooking)

        // Bind views
        tvRouteName = findViewById(R.id.tvRouteName)
        tvKm = findViewById(R.id.tvKm)
        tvAmount = findViewById(R.id.tvAmount)
        spinnerVia = findViewById(R.id.spinnerVia)
        btnConfirmBook = findViewById(R.id.btnConfirmBook)

        // Get data from intent
        studentId = intent.getIntExtra("studentid", -1)
        routeName = intent.getStringExtra("routename")

        Log.d("BusBooking", "StudentId: $studentId, RouteName: $routeName")

        if (routeName != null) {
            fetchBusDetails(routeName!!)
        } else {
            Toast.makeText(this, "Invalid bus details", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Handle booking button
        btnConfirmBook.setOnClickListener {
            if (busData != null && selectedBoarding != null) {
                Log.d("BusBooking", "Booking request → studentId=$studentId, route=${busData!!.routename}, boarding=$selectedBoarding")

                retrofit.instance.bookBus(
                    studentId,
                    busData!!.routename,
                    busData!!.amount,
                    busData!!.via,
                    selectedBoarding!!
                ).enqueue(object : Callback<BusConfirmResponse> {
                    override fun onResponse(
                        call: Call<BusConfirmResponse>,
                        response: Response<BusConfirmResponse>
                    ) {
                        Log.d("BusBooking", "Raw response: ${response.raw()}")
                        Log.d("BusBooking", "Body: ${response.body()}")
                        Log.d("BusBooking", "ErrorBody: ${response.errorBody()?.string()}")

                        if (response.isSuccessful && response.body()?.status == "success") {
                            Toast.makeText(
                                this@BusconfirmbookingActivity,
                                response.body()!!.message,
                                Toast.LENGTH_SHORT
                            ).show()

                            finish() // Go back
                        } else {
                            Toast.makeText(
                                this@BusconfirmbookingActivity,
                                response.body()?.message ?: "Booking failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<BusConfirmResponse>, t: Throwable) {
                        Log.e("BusBooking", "API call failed: ${t.message}", t)
                        Toast.makeText(
                            this@BusconfirmbookingActivity,
                            "Error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            } else {
                Toast.makeText(this, "Please select a boarding point", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchBusDetails(routename: String) {
        retrofit.instance.getBusDetails(routename).enqueue(object : Callback<BusResponse> {
            override fun onResponse(call: Call<BusResponse>, response: Response<BusResponse>) {
                Log.d("BusDetails", "Raw response: ${response.raw()}")
                Log.d("BusDetails", "Body: ${response.body()}")
                Log.d("BusDetails", "ErrorBody: ${response.errorBody()?.string()}")

                if (response.isSuccessful && response.body() != null) {
                    val busList = response.body()!!.data
                    if (busList.isNotEmpty()) {
                        busData = busList[0]

                        tvRouteName.text = busData!!.routename
                        tvKm.text = "${busData!!.km} KM"
                        tvAmount.text = "₹${busData!!.amount}"

                        val viaList = busData!!.via.split(",").map { it.trim() }
                        val adapter = ArrayAdapter(
                            this@BusconfirmbookingActivity,
                            android.R.layout.simple_spinner_dropdown_item,
                            viaList
                        )
                        spinnerVia.adapter = adapter

                        spinnerVia.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: android.view.View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    selectedBoarding = viaList[position]
                                    Log.d("BusBooking", "Selected boarding: $selectedBoarding")
                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                    selectedBoarding = null
                                }
                            }
                    }
                } else {
                    Toast.makeText(
                        this@BusconfirmbookingActivity,
                        "No bus details found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<BusResponse>, t: Throwable) {
                Log.e("BusDetails", "API call failed: ${t.message}", t)
                Toast.makeText(
                    this@BusconfirmbookingActivity,
                    "Error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
