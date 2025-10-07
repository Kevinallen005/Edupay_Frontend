package com.example.feepayment

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.feepayment.utils.MenuHandler

class PayoptionsActivity : AppCompatActivity() {

    private lateinit var overalldue : Button
    private lateinit var finrecord : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_adminstudoptions)

        val menuIcon = findViewById<ImageView>(R.id.menuIcon)
        AdminMenuHandler.setupMenu(this, menuIcon)

        overalldue = findViewById<Button>(R.id.duelist)
        overalldue.setOnClickListener {
            val studentId = intent.getIntExtra("studentId", -1)
            val intent = Intent(this , AdminsduelistActivity::class.java)
            intent.putExtra("studentId", studentId)
            startActivity(intent)
        }

        finrecord = findViewById<Button>(R.id.finrecord)
        finrecord.setOnClickListener {
            val studentId = intent.getIntExtra("studentId", -1) // Get from current screen's intent
            val intent = Intent(this, AdminpaymenthistoryActivity::class.java)
            intent.putExtra("studentId", studentId) // Pass it to next activity
            startActivity(intent)
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}