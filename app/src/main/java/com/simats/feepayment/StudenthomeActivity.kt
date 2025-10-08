package com.simats.feepayment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.simats.feepayment.adapters.DueListAdapterHome
import com.simats.feepayment.adapters.NotificationAdapter
import com.simats.feepayment.responses.DueItem
import com.simats.feepayment.responses.DueListResponse
import com.simats.feepayment.responses.NotificationItem
import com.simats.feepayment.responses.NotificationResponse
import com.simats.feepayment.responses.StudentHomeResponse
import com.simats.feepayment.retrofit.retrofit
import com.simats.feepayment.retrofit.retrofit.BASE_URL
import com.simats.feepayment.utils.MenuHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudenthomeActivity : AppCompatActivity() {

    private lateinit var historybtn: LinearLayout
    private lateinit var profilephoto: ImageView
    private lateinit var studName: TextView
    private lateinit var studclass: TextView
    private lateinit var section: TextView
    private lateinit var incharge: TextView
    private lateinit var emibtn : Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var noDueLayout: View
    private lateinit var dueListAdapter: DueListAdapterHome
    private var dueList = listOf<DueItem>()

    private lateinit var notificationRecyclerView: RecyclerView
    private lateinit var notificationAdapter: NotificationAdapter
    private var notificationList = listOf<NotificationItem>()

    private val slideHandler = Handler(Looper.getMainLooper())
    private var currentPosition = 0
    private var directionForward = true

    // Auto-scroll runnable with ping-pong looping
    private val slideRunnable = object : Runnable {
        override fun run() {
            val itemCount = recyclerView.adapter?.itemCount ?: 0
            if (itemCount <= 1) return

            // Move forward or backward
            if (directionForward) {
                currentPosition++
                if (currentPosition >= itemCount - 1) {
                    directionForward = false
                }
            } else {
                currentPosition--
                if (currentPosition <= 0) {
                    directionForward = true
                }
            }

            recyclerView.smoothScrollToPosition(currentPosition)
            slideHandler.postDelayed(this, 5000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_studenthome)

        val menuIcon = findViewById<ImageView>(R.id.menuIcon)
        MenuHandler.setupMenu(this, menuIcon)



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }





        // Bind all views
        historybtn = findViewById(R.id.historybtn)
        profilephoto = findViewById(R.id.profilephoto)
        studName = findViewById(R.id.studName)
        studclass = findViewById(R.id.studclass)
        section = findViewById(R.id.section)
        incharge = findViewById(R.id.incharge)
        recyclerView = findViewById(R.id.recyclerview)
        noDueLayout = findViewById(R.id.noDueLayout)
        emibtn = findViewById(R.id.emioptionsbtn)



        notificationRecyclerView = findViewById(R.id.recyclerview1)
        notificationRecyclerView.layoutManager = LinearLayoutManager(this)
        notificationAdapter = NotificationAdapter(notificationList)
        notificationRecyclerView.adapter = notificationAdapter

        fetchNotifications()


        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Setup adapter with empty list initially
        dueListAdapter = DueListAdapterHome(this, dueList) { dueItem ->
            val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString("feename", dueItem.feename)
                apply()
            }
            startActivity(Intent(this, PaymentportalActivity::class.java))
        }
        recyclerView.adapter = dueListAdapter

        // Start auto-scroll handler
        slideHandler.postDelayed(slideRunnable, 5000)

        // On click: open payment history
        historybtn.setOnClickListener {
            startActivity(Intent(this, PaymenthistoryActivity::class.java))
        }

        emibtn.setOnClickListener {
            startActivity(Intent(this, SplitoptionsActivity::class.java))
        }

        // Navigate to due list
        findViewById<LinearLayout>(R.id.payhome).setOnClickListener {
            val studentId = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                .getInt("studentid", -1)
            if (studentId != -1) {
                startActivity(Intent(this, StudentduelistActivity::class.java))
            } else {
                Toast.makeText(this, "Student ID not found", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.feeduelistbtn).setOnClickListener {
            val studentId = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                .getInt("studentid", -1)
            if (studentId != -1) {
                startActivity(Intent(this, StudentduelistActivity::class.java))
            } else {
                Toast.makeText(this, "Student ID not found", Toast.LENGTH_SHORT).show()
            }
        }

        // Navigate to profile
        findViewById<LinearLayout>(R.id.profilelayout).setOnClickListener {
            val studentId = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                .getInt("studentid", -1)
            if (studentId != -1) {
                val intent = Intent(this, StudentprofileActivity::class.java)
                intent.putExtra("studentid", studentId)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Student ID not found", Toast.LENGTH_SHORT).show()
            }
        }

        // Get student ID and load data
        val studentId = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            .getInt("studentid", -1)
        if (studentId != -1) {
            fetchStudentDetails(studentId)
            fetchDueList(studentId)
        } else {
            Toast.makeText(this, "Student ID not found", Toast.LENGTH_SHORT).show()
        }
    }




    private fun fetchStudentDetails(studentId: Int) {
        retrofit.instance.getStudentHomeData(studentId)
            .enqueue(object : Callback<StudentHomeResponse> {
                override fun onResponse(
                    call: Call<StudentHomeResponse>,
                    response: Response<StudentHomeResponse>
                ) {
                    if (response.isSuccessful && response.body()?.status == "success") {
                        val profile = response.body()?.profile
                        profile?.let {
                            studName.text = "Name : ${it.name}"
                            studclass.text = "Class : ${it.class_}"
                            section.text = "Sec: ${it.sec}"
                            incharge.text = "Incharge: ${it.incharge}"

                            profilephoto.setImageResource(R.drawable.baseline_account_circle_24)
                            Glide.with(this@StudenthomeActivity)
                                .load("${BASE_URL}${it.photo}")
                                .placeholder(R.drawable.baseline_account_circle_24)
                                .error(R.drawable.baseline_account_circle_24)
                                .into(profilephoto)
                        }
                    } else {
                        Toast.makeText(
                            this@StudenthomeActivity,
                            "Profile not found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<StudentHomeResponse>, t: Throwable) {
                    Toast.makeText(
                        this@StudenthomeActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }


    private fun fetchNotifications() {
        val studentId = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            .getInt("studentid", -1)

        if (studentId == -1) {
            Toast.makeText(this, "Student ID not found", Toast.LENGTH_SHORT).show()
            return
        }

        retrofit.instance.getnotifications(studentId)
            .enqueue(object : Callback<NotificationResponse> {
                override fun onResponse(
                    call: Call<NotificationResponse>,
                    response: Response<NotificationResponse>
                ) {
                    if (response.isSuccessful && response.body()?.status == "success") {
                        val notificationList = response.body()?.notifications ?: emptyList()
                        notificationAdapter = NotificationAdapter(notificationList)
                        notificationRecyclerView.adapter = notificationAdapter
                    }
                }

                override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
                    Toast.makeText(this@StudenthomeActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }




    private fun fetchDueList(studentId: Int) {
        retrofit.instance.getDueList(studentId)
            .enqueue(object : Callback<DueListResponse> {
                override fun onResponse(
                    call: Call<DueListResponse>,
                    response: Response<DueListResponse>
                ) {
                    if (response.isSuccessful && response.body()?.status == "success") {
                        dueList = response.body()?.data ?: emptyList()

                        if (dueList.isEmpty()) {
                            recyclerView.visibility = View.GONE
                            noDueLayout.visibility = View.VISIBLE
                            return
                        } else {
                            recyclerView.visibility = View.VISIBLE
                            noDueLayout.visibility = View.GONE
                        }

                        dueListAdapter = DueListAdapterHome(this@StudenthomeActivity, dueList) { dueItem ->
                            val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                            with(sharedPref.edit()) {
                                putString("feename", dueItem.feename)
                                apply()
                            }
                            startActivity(Intent(this@StudenthomeActivity, PaymentportalActivity::class.java))
                        }
                        recyclerView.adapter = dueListAdapter

                        currentPosition = 0
                        recyclerView.scrollToPosition(currentPosition)
                    } else {
                        recyclerView.visibility = View.GONE
                        noDueLayout.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<DueListResponse>, t: Throwable) {
                    Toast.makeText(this@StudenthomeActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        slideHandler.removeCallbacks(slideRunnable)
    }
}
