package com.example.feepayment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.feepayment.adapters.AdminDueListAdapter
import com.example.feepayment.adapters.DueListAdapter
import com.example.feepayment.responses.DueItem
import com.example.feepayment.responses.DueListResponse
import com.example.feepayment.retrofit.retrofit
import com.example.feepayment.utils.MenuHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminsduelistActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_adminsduelist)


        val menuIcon = findViewById<ImageView>(R.id.menuIcon)
        AdminMenuHandler.setupMenu(this, menuIcon)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val studentid = intent.getIntExtra("studentId", -1)
        Log.d("PaymentDebug", "Student ID from Intent: $studentid")

        if (studentid != -1) {
            fetchDueList(studentid)
        } else {
            Toast.makeText(this, "Student ID not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchDueList(studentId: Int) {
        retrofit.instance.getDueList(studentId).enqueue(object : Callback<DueListResponse> {
            override fun onResponse(call: Call<DueListResponse>, response: Response<DueListResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    val list: List<DueItem> = response.body()?.data ?: emptyList()

                    if (list.isEmpty()) {
                        // Show custom toast
                        val layout = layoutInflater.inflate(R.layout.custom_toast, null)
                        val textView = layout.findViewById<TextView>(R.id.toastText)
                        textView.text = "NO DUE"

                        val toast = Toast(this@AdminsduelistActivity)
                        toast.duration = Toast.LENGTH_SHORT
                        toast.view = layout
                        toast.show()
                    } else {
                        // Show RecyclerView
                        val adapter = AdminDueListAdapter(this@AdminsduelistActivity, list) { dueItem ->
                            showStudentOptionsDialog(studentId, dueItem)
                        }
                        recyclerView.adapter = adapter
                    }
                } else {
                    showCustomToast("No due list found")
                }
            }

            override fun onFailure(call: Call<DueListResponse>, t: Throwable) {
                showCustomToast("Error: ${t.message}")
            }
        })
    }

    private fun showCustomToast(message: String) {
        val layout = layoutInflater.inflate(R.layout.custom_toast, null)
        val textView = layout.findViewById<TextView>(R.id.toastText)
        textView.text = message

        val toast = Toast(this)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
    }

    private fun showStudentOptionsDialog(studentId: Int, dueItem: DueItem) {
        val dialogView = LayoutInflater.from(this)
            .inflate(R.layout.studentoptionstoggle2, null, false)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(true)

        dialogView.findViewById<TextView>(R.id.scholarship)
            .setOnClickListener {
                val intent = Intent(this, ScholarshipActivity::class.java)
                intent.putExtra("studentId", studentId) // ✅ use Activity's ID
                startActivity(intent)
                dialog.dismiss()
            }

//        dialogView.findViewById<TextView>(R.id.removestudent)
//            .setOnClickListener {
//                // Remove student logic here
//                dialog.dismiss()
//            }

        dialog.show()
    }


}
