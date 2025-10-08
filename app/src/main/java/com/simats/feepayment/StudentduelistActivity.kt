package com.simats.feepayment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simats.feepayment.adapters.DueListAdapter
import com.simats.feepayment.responses.DueItem
import com.simats.feepayment.responses.DueListResponse
import com.simats.feepayment.retrofit.retrofit
import com.simats.feepayment.utils.MenuHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudentduelistActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_studentduelist)


        val menuIcon = findViewById<ImageView>(R.id.menuIcon)
        MenuHandler.setupMenu(this, menuIcon)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val studentId = sharedPref.getInt("studentid", -1)

        if (studentId != -1) {
            fetchDueList(studentId)
        } else {
            Toast.makeText(this, "Student ID not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchDueList(studentId: Int) {
        retrofit.instance.getDueList(studentId).enqueue(object : Callback<DueListResponse> {
            override fun onResponse(call: Call<DueListResponse>, response: Response<DueListResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    val list: List<DueItem> = response.body()?.data ?: emptyList()
                    val adapter = DueListAdapter(this@StudentduelistActivity, list) {
                        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putString("feename", it.feename)
                            apply()
                        }
                        val intent = Intent(this@StudentduelistActivity, PaymentportalActivity::class.java)
                        startActivity(intent)

                        // Handle actual payment action here
                    }
                    recyclerView.adapter = adapter
                } else {
                    Toast.makeText(this@StudentduelistActivity, "No due list found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DueListResponse>, t: Throwable) {
                Toast.makeText(this@StudentduelistActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
