package com.simats.feepayment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.simats.feepayment.responses.FeeItem
import com.simats.feepayment.responses.FeesDueResponse
import com.simats.feepayment.responses.InchargeResponse
import com.simats.feepayment.responses.QuotaItem
import com.simats.feepayment.responses.QuotaResponse
import com.simats.feepayment.responses.StudentProfileResponse
import com.simats.feepayment.retrofit.retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScholarshipActivity : AppCompatActivity() {

    private lateinit var name: TextView
    private lateinit var id: TextView
    private lateinit var feeNameButton: AppCompatButton
    private var feeList: List<FeeItem> = emptyList()
    private lateinit var amountButton: AppCompatButton
    private var quotaList: List<QuotaItem> = emptyList()
    private lateinit var otherQuotaLayout: LinearLayout
    private lateinit var otherQuotaName: EditText
    private lateinit var otherQuotaPercentage: EditText
    private lateinit var inchargeButton: AppCompatButton
    private var inchargeList: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_scholarship)

        val menuIcon = findViewById<ImageView>(R.id.menuIcon)
        AdminMenuHandler.setupMenu(this, menuIcon)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Bind views
        name = findViewById(R.id.studentName)
        id = findViewById(R.id.studentId)
        feeNameButton = findViewById(R.id.feenameButton)
        amountButton = findViewById(R.id.amountButton)
        otherQuotaLayout = findViewById(R.id.otherQuotaLayout)
        otherQuotaName = findViewById(R.id.otherQuotaName)
        otherQuotaPercentage = findViewById(R.id.otherQuotaPercentage)
        inchargeButton = findViewById(R.id.inchargeButton)

        val studentId = intent.getIntExtra("studentId", -1)
        Log.d("ScholarshipActivity", "Received studentId: $studentId")

        if (studentId != -1) {
            loadStudentBasicInfo(studentId)
            loadFeesList(studentId)
            loadQuotaList(studentId)
            loadInchargeList()
        } else {
            Toast.makeText(this, "No student ID provided", Toast.LENGTH_SHORT).show()
        }

        feeNameButton.setOnClickListener { showFeeDropdown() }
        amountButton.setOnClickListener { showQuotaDropdown() }
        inchargeButton.setOnClickListener { showInchargeDropdown() }

        val applyButton = findViewById<Button>(R.id.applyButton)
        applyButton.setOnClickListener {
            val feeNameText = feeNameButton.text.toString()
            val quotaText = amountButton.text.toString()
            val inchargeText = inchargeButton.text.toString()

            var quotaName = ""
            var percentage = ""



            if (quotaText == "Other") {
                quotaName = otherQuotaName.text.toString()
                percentage = otherQuotaPercentage.text.toString()
            } else {
                val parts = quotaText.split(" - ")
                quotaName = parts[0]
                percentage = parts.getOrNull(1)?.replace("%", "") ?: ""
            }

            if (studentId == -1 || quotaName.isBlank() || percentage.isBlank() || feeNameText.isBlank() || inchargeText.isBlank()) {
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val feeName = feeNameText.split(" - ")[0]

            // Only go to confirmation screen — don't update DB yet
            val classNo = intent.getIntExtra("classNumber", -1)
            val confirmIntent = Intent(this@ScholarshipActivity, AsconfirmationActivity::class.java)
            confirmIntent.putExtra("studentId", studentId)
            confirmIntent.putExtra("quotaName", quotaName)
            confirmIntent.putExtra("percentage", percentage)
            confirmIntent.putExtra("feeName", feeName)
            confirmIntent.putExtra("incharge", inchargeText)
            confirmIntent.putExtra("classNo", classNo)
            startActivity(confirmIntent)
        }
    }

    private fun loadInchargeList() {
        retrofit.instance.getInchargeList()
            .enqueue(object : Callback<InchargeResponse> {
                override fun onResponse(
                    call: Call<InchargeResponse>,
                    response: Response<InchargeResponse>
                ) {
                    if (response.isSuccessful && response.body()?.status == "success") {
                        inchargeList = response.body()?.data?.map { it.incharge } ?: emptyList()
                    } else {
                        Toast.makeText(this@ScholarshipActivity, "No incharge data found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<InchargeResponse>, t: Throwable) {
                    Toast.makeText(this@ScholarshipActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showInchargeDropdown() {
        if (inchargeList.isEmpty()) {
            Toast.makeText(this, "No incharges available", Toast.LENGTH_SHORT).show()
            return
        }

        val wrapper = ContextThemeWrapper(this, R.style.CustomPopupMenu)
        val popupMenu = PopupMenu(wrapper, inchargeButton)

        inchargeList.forEach { name -> popupMenu.menu.add(name) }

        popupMenu.setOnMenuItemClickListener { item ->
            inchargeButton.text = item.title
            true
        }

        popupMenu.show()
    }

    private fun loadStudentBasicInfo(studentId: Int) {
        retrofit.instance.getStudentProfile(studentId)
            .enqueue(object : Callback<StudentProfileResponse> {
                override fun onResponse(
                    call: Call<StudentProfileResponse>,
                    response: Response<StudentProfileResponse>
                ) {
                    Log.d("ScholarshipActivity", "StudentProfile response: ${response.body()}")
                    if (response.isSuccessful && response.body() != null) {
                        val data = response.body()!!
                        name.text = data.name
                        id.text = "ID: $studentId"
                    } else {
                        Toast.makeText(this@ScholarshipActivity, "Failed to load data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<StudentProfileResponse>, t: Throwable) {
                    Log.e("ScholarshipActivity", "Error loading student profile", t)
                    Toast.makeText(this@ScholarshipActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun loadFeesList(studentId: Int) {
        retrofit.instance.getduefeeslist(studentId)
            .enqueue(object : Callback<FeesDueResponse> {
                override fun onResponse(
                    call: Call<FeesDueResponse>,
                    response: Response<FeesDueResponse>
                ) {
                    Log.d("ScholarshipActivity", "FeesDueResponse raw: ${response.body()}")
                    if (response.isSuccessful && response.body()?.status == "success") {
                        feeList = response.body()?.data ?: emptyList()
                    } else {
                        val classNo = intent.getIntExtra("classNumber", -1)
                        Toast.makeText(this@ScholarshipActivity, "Student Paid All Fees", Toast.LENGTH_SHORT).show()

                        if (classNo != -1) {
                            val backIntent = Intent(this@ScholarshipActivity, DatabasestudsActivity::class.java)
                            backIntent.putExtra("classNumber", classNo)
                            backIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(backIntent)
                        }
                        finish()

                    }


                }

                override fun onFailure(call: Call<FeesDueResponse>, t: Throwable) {
                    Toast.makeText(this@ScholarshipActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showFeeDropdown() {
        if (feeList.isEmpty()) {
            Toast.makeText(this, "No fees available", Toast.LENGTH_SHORT).show()
            return
        }

        val wrapper = ContextThemeWrapper(this, R.style.CustomPopupMenu)
        val popupMenu = PopupMenu(wrapper, feeNameButton)

        feeList.forEach { fee -> popupMenu.menu.add("${fee.feename} - ₹${fee.feeamt}") }

        popupMenu.setOnMenuItemClickListener { item ->
            feeNameButton.text = item.title
            true
        }

        popupMenu.show()
    }

    private fun loadQuotaList(studentId: Int) {
        retrofit.instance.getQuotaList(studentId)
            .enqueue(object : Callback<QuotaResponse> {
                override fun onResponse(
                    call: Call<QuotaResponse>,
                    response: Response<QuotaResponse>
                ) {
                    if (response.isSuccessful && response.body()?.status == "success") {
                        quotaList = response.body()?.data ?: emptyList()
                    } else {
                        Toast.makeText(this@ScholarshipActivity, "No quota data found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<QuotaResponse>, t: Throwable) {
                    Toast.makeText(this@ScholarshipActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showQuotaDropdown() {
        if (quotaList.isEmpty()) {
            Toast.makeText(this, "No quotas available", Toast.LENGTH_SHORT).show()
            return
        }

        val wrapper = ContextThemeWrapper(this, R.style.CustomPopupMenu)
        val popupMenu = PopupMenu(wrapper, amountButton)

        quotaList.forEach { quota -> popupMenu.menu.add("${quota.quota} - ${quota.percentage}%") }
        popupMenu.menu.add("Other")

        popupMenu.setOnMenuItemClickListener { item ->
            amountButton.text = item.title
            if (item.title == "Other") {
                otherQuotaLayout.visibility = View.VISIBLE
            } else {
                otherQuotaLayout.visibility = View.GONE
            }
            true
        }

        popupMenu.show()
    }
}
