package com.example.feepayment

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.feepayment.responses.StudentProfileResponse
import com.example.feepayment.retrofit.retrofit
import com.example.feepayment.utils.MenuHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudentprofileActivity : AppCompatActivity() {

    private lateinit var name: TextView
    private lateinit var class_: TextView
    private lateinit var sect: TextView
    private lateinit var incharge: TextView
    private lateinit var inchargeno: TextView
    private lateinit var mail: TextView
    private lateinit var blood: TextView
    private lateinit var father: TextView
    private lateinit var fatherno: TextView
    private lateinit var mother: TextView
    private lateinit var motherno: TextView
    private lateinit var photo: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_studentprofile)

        val menuIcon = findViewById<ImageView>(R.id.menuIcon)
        MenuHandler.setupMenu(this, menuIcon)

        val homeIcon = findViewById<ImageView>(R.id.homeicon)

        homeIcon.setOnClickListener {
            val intent = Intent(this, StudenthomeActivity::class.java)
            startActivity(intent)
        }

        // Bind views
        name = findViewById(R.id.name)
        class_ = findViewById(R.id.class_)
        sect = findViewById(R.id.sect)
        incharge = findViewById(R.id.incharge)
        inchargeno = findViewById(R.id.inchargeno)
        mail = findViewById(R.id.mail)
        blood = findViewById(R.id.blood)
        father = findViewById(R.id.father)
        fatherno = findViewById(R.id.fatherno)
        mother = findViewById(R.id.mother)
        motherno = findViewById(R.id.motherno)
        photo = findViewById(R.id.photo)

        val studentId = intent.getIntExtra("studentid", -1)
        if (studentId != -1) {
            loadStudentProfile(studentId)
        } else {
            Toast.makeText(this, "No student ID provided", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadStudentProfile(studentId: Int) {
        retrofit.instance.getStudentProfile(studentId)
            .enqueue(object : Callback<StudentProfileResponse> {
                override fun onResponse(
                    call: Call<StudentProfileResponse>,
                    response: Response<StudentProfileResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val data = response.body()!!

                        name.text = data.name
                        class_.text = data.class_
                        sect.text = data.sec
                        incharge.text = data.incharge ?: "N/A"
                        inchargeno.text = data.inchargeno ?: "N/A"
                        mail.text = data.email
                        blood.text = data.bloodgroup
                        father.text = data.fathername
                        fatherno.text = data.fatherno
                        mother.text = data.mothername
                        motherno.text = data.motherno

                        val photoUrl = retrofit.BASE_URL + data.photo
                        Glide.with(this@StudentprofileActivity).load(photoUrl).into(photo)
                    } else {
                        Toast.makeText(this@StudentprofileActivity, "Failed to load data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<StudentProfileResponse>, t: Throwable) {
                    Toast.makeText(this@StudentprofileActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
