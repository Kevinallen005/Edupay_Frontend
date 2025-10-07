package com.example.feepayment

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.feepayment.responses.BusPassResponse
import com.example.feepayment.retrofit.retrofit
import com.example.feepayment.utils.MenuHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MybuspassActivity : AppCompatActivity() {

    private lateinit var photo: ImageView
    private lateinit var tvStudentName: TextView
    private lateinit var tvStudentId: TextView
    private lateinit var tvRouteName: TextView
    private lateinit var tvBoardingPoint: TextView
    private lateinit var tvAmount: TextView
    private lateinit var tvVia: TextView
    private lateinit var tvValidFrom: TextView
    private lateinit var tvValidUntil: TextView
    private lateinit var statuspass: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mybuspass)

        // Bind Views
        photo = findViewById(R.id.photo)
        tvStudentName = findViewById(R.id.tvStudentName)
        tvStudentId = findViewById(R.id.tvStudentId)
        tvRouteName = findViewById(R.id.tvRouteName)
        tvBoardingPoint = findViewById(R.id.tvBoardingPoint)
        tvAmount = findViewById(R.id.tvAmount)
        tvVia = findViewById(R.id.tvVia)
        tvValidFrom = findViewById(R.id.tvvalidFrom)
        tvValidUntil = findViewById(R.id.tvvalidUntil)
        statuspass = findViewById(R.id.statuspass)

        // Setup Menu
        val menuIcon = findViewById<ImageView>(R.id.menuIcon)
        MenuHandler.setupMenu(this, menuIcon)

        // Home icon click
        val homeIcon = findViewById<ImageView>(R.id.homeicon)
        homeIcon.setOnClickListener {
            val intent = Intent(this, StudenthomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        // Get student ID from intent
        val studentId = intent.getIntExtra("studentid", -1)
        if (studentId != -1) {
            loadBusPass(studentId)
        } else {
            Toast.makeText(this, "No student ID provided", Toast.LENGTH_SHORT).show()
            goToHome()
        }
    }

    private fun loadBusPass(studentId: Int) {
        retrofit.instance.getBusPass(studentId)
            .enqueue(object : Callback<BusPassResponse> {
                override fun onResponse(call: Call<BusPassResponse>, response: Response<BusPassResponse>) {
                    if (response.isSuccessful && response.body() != null && response.body()!!.success) {
                        val data = response.body()!!.data!!

                        // Set TextViews
                        tvStudentName.text = data.name
                        tvStudentId.text = "Student ID: ${data.studentid}"
                        tvRouteName.text = "Route: ${data.routename}"
                        tvBoardingPoint.text = "Boarding Point: ${data.boardingPoint}"
                        tvAmount.text = "Fare Paid: ₹ ${data.amount}"
                        tvVia.text = "Via: ${data.via}"
                        tvValidFrom.text = "Valid From: ${data.validFrom}"
                        tvValidUntil.text = "Valid Until: ${data.validUntil}"
                        statuspass.text = data.status.uppercase()

                        // Set background based on status
                        when (data.status.lowercase()) {
                            "accepted" -> statuspass.setBackgroundResource(R.drawable.curved_border_green)
                            "pending" -> statuspass.setBackgroundResource(R.drawable.curved_border_red)
                            else -> statuspass.setBackgroundResource(R.drawable.curved_border_lightgrey)
                        }


                        val photoUrl = retrofit.BASE_URL + data.photo
                        Glide.with(this@MybuspassActivity).load(photoUrl).into(photo)
                    } else {
                        Toast.makeText(this@MybuspassActivity, "No Bus Pass Requests", Toast.LENGTH_SHORT).show()
                        goToHome()
                    }
                }

                override fun onFailure(call: Call<BusPassResponse>, t: Throwable) {
                    Toast.makeText(this@MybuspassActivity, "API Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    goToHome()
                }
            })
    }

    // Override back button to go to home
    override fun onBackPressed() {
        goToHome()
    }

    private fun goToHome() {
        val intent = Intent(this, StudenthomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }
}
