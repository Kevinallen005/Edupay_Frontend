package com.example.feepayment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.feepayment.responses.AdminHomeResponse
import com.example.feepayment.retrofit.retrofit
import com.example.feepayment.utils.MenuHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminhomeActivity : AppCompatActivity() {

    // Declare all UI elements
    private lateinit var schoolName: TextView
    private lateinit var feeImposed: TextView
    private lateinit var feeCollected: TextView
    private lateinit var feeScholarship: TextView
    private lateinit var feeDue: TextView
    private lateinit var totalStudents: TextView
    private lateinit var defaultersBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_adminhome)

        val menuIcon = findViewById<ImageView>(R.id.menuIcon)
        AdminMenuHandler.setupMenu(this, menuIcon)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Bind views to IDs in XML
        schoolName = findViewById(R.id.schoolname)
        feeImposed = findViewById(R.id.feeimposed)
        feeCollected = findViewById(R.id.feecollected)
        feeScholarship = findViewById(R.id.scholarship)
        feeDue = findViewById(R.id.feedue)
        totalStudents = findViewById(R.id.totalstudents)
        defaultersBtn = findViewById(R.id.defaultersbtn)



        defaultersBtn.setOnClickListener {
            val intent = Intent(this, DefaultersdbActivity::class.java)
            startActivity(intent)
        }

        // Get stored admin ID from SharedPreferences
        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val studentId = sharedPref.getInt("studentid", -1)

        if (studentId != -1) {
            fetchAdminDashboard(studentId)
        } else {
            Toast.makeText(this, "Admin ID not found", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to make API call
    private fun fetchAdminDashboard(studentId: Int) {
        retrofit.instance.getAdminDashboard(studentId)
            .enqueue(object : Callback<AdminHomeResponse> {
                override fun onResponse(
                    call: Call<AdminHomeResponse>,
                    response: Response<AdminHomeResponse>
                ) {
                    if (response.isSuccessful && response.body()?.status == "success") {
                        val data = response.body()!!
                        schoolName.text = data.school_name
                        feeImposed.text = "₹${data.fee.imposed}"
                        feeCollected.text = "₹${data.fee.collected}"
                        feeScholarship.text = "₹${data.fee.scholarship}"
                        feeDue.text = "₹${data.fee.due}"
                        totalStudents.text = "${data.total_students}"
                    } else {
                        Toast.makeText(this@AdminhomeActivity, "Failed to load data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<AdminHomeResponse>, t: Throwable) {
                    Toast.makeText(this@AdminhomeActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
