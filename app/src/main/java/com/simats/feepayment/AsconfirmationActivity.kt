package com.simats.feepayment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.simats.feepayment.responses.GenericResponse
import com.simats.feepayment.retrofit.retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AsconfirmationActivity : AppCompatActivity() {

    private lateinit var yesButton: Button
    private lateinit var noButton: Button
    private lateinit var confirmText: TextView

    private var studentId: Int = -1
    private var schname: String = ""
    private var percentage: String = ""
    private var incharge: String = ""
    private var feename: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_asconfirmation)

        yesButton = findViewById(R.id.yesBtn)
        noButton = findViewById(R.id.noBtn)
        confirmText = findViewById(R.id.confirmText)

        // Get passed data
        studentId = intent.getIntExtra("studentId", -1)
        schname = intent.getStringExtra("quotaName") ?: ""
        percentage = intent.getStringExtra("percentage") ?: ""
        incharge = intent.getStringExtra("incharge") ?: ""
        feename = intent.getStringExtra("feeName") ?: ""
        val classNo = intent.getIntExtra("classNo", -1)

        confirmText.text = "Apply scholarship '$schname' of $percentage% for $feename?"

        yesButton.setOnClickListener {
            applyScholarship()
        }

        noButton.setOnClickListener {
            showCustomToast("Cancelled", R.drawable.removestud)
            navigateBack()
        }
    }

    private fun applyScholarship() {
        if (studentId == -1 || schname.isBlank() || percentage.isBlank() || feename.isBlank() || incharge.isBlank()) {
            Toast.makeText(this, "Invalid scholarship details", Toast.LENGTH_SHORT).show()
            return
        }

        retrofit.instance.applyScholarship(
            studentId,
            schname,
            percentage,
            feename,
            incharge
        ).enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    showCustomToast("Scholarship applied successfully", R.drawable.scholarship)
                    navigateBack()
                } else {
                    Toast.makeText(this@AsconfirmationActivity, "Failed: ${response.body()?.message ?: "Unknown error"}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                Toast.makeText(this@AsconfirmationActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateBack() {
        val classNo = intent.getIntExtra("classNo", -1) // make sure ScholarshipActivity sends "classNo"
        val backIntent = Intent(this, DatabasestudsActivity::class.java)
        backIntent.putExtra("classNumber", classNo) // ✅ same key that DatabasestudsActivity uses
        backIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(backIntent)
        finish()
    }




    private fun showCustomToast(message: String, iconResId: Int) {
        val inflater = LayoutInflater.from(this)
        val layout = inflater.inflate(R.layout.custom_toast, null)

        val toastIcon = layout.findViewById<ImageView>(R.id.toastIcon)
        val toastText = layout.findViewById<TextView>(R.id.toastText)

        toastIcon.setImageResource(iconResId)
        toastText.text = message

        Toast(this).apply {
            duration = Toast.LENGTH_SHORT
            view = layout
        }.show()
    }
}
