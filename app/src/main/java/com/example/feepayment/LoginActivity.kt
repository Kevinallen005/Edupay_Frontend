package com.example.feepayment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.feepayment.retrofit.retrofit
import com.example.feepayment.responses.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var loginbtn: Button
    private lateinit var UsernameField: EditText
    private lateinit var PasswordField: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginbtn = findViewById(R.id.loginbtn)
        UsernameField = findViewById(R.id.editUsername)
        PasswordField = findViewById(R.id.editPassword)

        loginbtn.setOnClickListener {
            val username = UsernameField.text.toString().trim()
            val password = PasswordField.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            } else {
                checkLogin(username, password)
            }
        }
    }

    private fun checkLogin(username: String, password: String) {
        retrofit.instance.login(username, password)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val loginResponse = response.body()!!
                        if (loginResponse.status == "success") {
                            val user = loginResponse.user
                            if (user != null) {
                                val studentId = user.studentid
                                val role = user.role

                                val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                                sharedPref.edit().putInt("studentid", studentId).apply()

                                if (role == "admin") {
                                    val intent = Intent(this@LoginActivity, AdminhomeActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    val intent = Intent(this@LoginActivity, StudenthomeActivity::class.java)
                                    intent.putExtra("studentName", user.name)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        } else {
                            Toast.makeText(this@LoginActivity, loginResponse.message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Invalid server response", Toast.LENGTH_SHORT).show()
                        Log.e("Login", "Unsuccessful response: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.e("LoginError", t.toString())
                }
            })
    }

}
