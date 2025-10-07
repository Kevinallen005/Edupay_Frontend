package com.example.feepayment

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MaindatabaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_maindatabase)

        val menuIcon = findViewById<ImageView>(R.id.menuIcon)
        AdminMenuHandler.setupMenu(this, menuIcon)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.viewbtn1).setOnClickListener { launchClass(1) }
        findViewById<Button>(R.id.viewbtn2).setOnClickListener { launchClass(2) }
        findViewById<Button>(R.id.viewbtn3).setOnClickListener { launchClass(3) }
        findViewById<Button>(R.id.viewbtn4).setOnClickListener { launchClass(4) }
        findViewById<Button>(R.id.viewbtn5).setOnClickListener { launchClass(5) }
        findViewById<Button>(R.id.viewbtn6).setOnClickListener { launchClass(6) }
        findViewById<Button>(R.id.viewbtn7).setOnClickListener { launchClass(7) }
        findViewById<Button>(R.id.viewbtn8).setOnClickListener { launchClass(8) }
        findViewById<Button>(R.id.viewbtn9).setOnClickListener { launchClass(9) }
        findViewById<Button>(R.id.viewbtn10).setOnClickListener { launchClass(10) }
        findViewById<Button>(R.id.viewbtn11).setOnClickListener { launchClass(11) }
        findViewById<Button>(R.id.viewbtn12).setOnClickListener { launchClass(12) }
    }

    private fun launchClass(classNum: Int) {
        val intent = Intent(this, DatabasestudsActivity::class.java)
        intent.putExtra("classNumber", classNum)
        startActivity(intent)
    }
}
