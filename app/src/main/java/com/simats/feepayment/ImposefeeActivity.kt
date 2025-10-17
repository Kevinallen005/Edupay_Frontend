package com.simats.feepayment

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.simats.feepayment.responses.ImposefeeResponse
import com.simats.feepayment.responses.ImposeFeeSubmitResponse
import com.simats.feepayment.responses.StudentInfo
import com.simats.feepayment.retrofit.retrofit
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ImposefeeActivity : AppCompatActivity() {

    private lateinit var studentContainer: LinearLayout
    private lateinit var feeNameEdit: EditText
    private lateinit var feeAmtEdit: EditText
    private lateinit var dueDateEdit: EditText
    private lateinit var applyButton: Button

    private var selectedIds: ArrayList<Int> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_imposefee)

        val menuIcon = findViewById<ImageView>(R.id.menuIcon)
        AdminMenuHandler.setupMenu(this, menuIcon)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        studentContainer = findViewById(R.id.studentcontainer)
        feeNameEdit = findViewById(R.id.feename)
        feeAmtEdit = findViewById(R.id.amount)
        dueDateEdit = findViewById(R.id.duedate)
        applyButton = findViewById(R.id.applyButton)

        // Get selected IDs from Intent
        selectedIds = intent.getIntegerArrayListExtra("selectedStudentIds") ?: arrayListOf()
        Log.d("ImposefeeActivity", "Received IDs: $selectedIds")

        if (selectedIds.isEmpty()) {
            Toast.makeText(this, "No students selected", Toast.LENGTH_SHORT).show()
            return
        }

        // Fetch & show students
        fetchStudentsByIds(selectedIds)

        // Date picker for dueDate field
        dueDateEdit.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                this,
                { _, y, m, d ->
                    val dateStr = "$y-${m + 1}-$d"
                    dueDateEdit.setText(dateStr)
                },
                year, month, day
            )
            datePicker.show()
        }

        // Apply button → call PHP
        applyButton.setOnClickListener {
            val feeName = feeNameEdit.text.toString().trim()
            val feeAmt = feeAmtEdit.text.toString().trim()
            val dueDate = dueDateEdit.text.toString().trim()

            if (feeName.isEmpty() || feeAmt.isEmpty() || dueDate.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amountValue = feeAmt.toDoubleOrNull()
            if (amountValue == null || amountValue <= 10) {
                Toast.makeText(this, "Amount must be greater than 10", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val idsJson = Gson().toJson(selectedIds) // convert list → JSON string

            retrofit.instance.imposeFee(feeName, feeAmt, dueDate, idsJson)
                .enqueue(object : Callback<ImposeFeeSubmitResponse> {
                    override fun onResponse(
                        call: Call<ImposeFeeSubmitResponse>,
                        response: Response<ImposeFeeSubmitResponse>
                    ) {
                        if (response.isSuccessful) {
                            val body = response.body()
                            if (body?.status == "success") {
                                Toast.makeText(this@ImposefeeActivity, body.message, Toast.LENGTH_LONG).show()
                                Log.d("ImposefeeActivity", "✅ Fee imposed: ${body.message}")
                                finish()
                            } else {
                                Toast.makeText(this@ImposefeeActivity, body?.message ?: "Error", Toast.LENGTH_SHORT).show()
                                Log.e("ImposefeeActivity", "❌ Error: ${body?.message}")
                            }
                        } else {
                            Toast.makeText(this@ImposefeeActivity, "Response failed", Toast.LENGTH_SHORT).show()
                            Log.e("ImposefeeActivity", "❌ Response failed: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<ImposeFeeSubmitResponse>, t: Throwable) {
                        Toast.makeText(this@ImposefeeActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        Log.e("ImposefeeActivity", "❌ Failure: ${t.message}")
                    }
                })
        }
    }

    private fun fetchStudentsByIds(ids: List<Int>) {
        retrofit.instance.getStudentsByIds(ids)
            .enqueue(object : Callback<ImposefeeResponse> {
                override fun onResponse(
                    call: Call<ImposefeeResponse>,
                    response: Response<ImposefeeResponse>
                ) {
                    if (response.isSuccessful && response.body()?.status == "success") {
                        val students = response.body()?.data ?: emptyList()
                        showStudents(students)
                    } else {
                        Toast.makeText(this@ImposefeeActivity, "Failed to fetch students", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ImposefeeResponse>, t: Throwable) {
                    Toast.makeText(this@ImposefeeActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showStudents(students: List<StudentInfo>) {
        studentContainer.removeAllViews()

        for (student in students) {
            val view = LayoutInflater.from(this)
                .inflate(R.layout.imposestud, studentContainer, false)

            val nameText = view.findViewById<TextView>(R.id.studentName)
            val idText = view.findViewById<TextView>(R.id.studentId)

            nameText.text = student.name
            idText.text = "ID: ${student.studentid}"

            studentContainer.addView(view)
        }
    }
}
