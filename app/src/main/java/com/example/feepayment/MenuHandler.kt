// File: MenuHandler.kt
package com.example.feepayment.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import com.example.feepayment.*

import com.example.feepayment.R // ensure correct import

object MenuHandler {

    fun setupMenu(activity: Activity, icon: ImageView) {
        icon.setOnClickListener {
            val popupMenu = PopupMenu(activity, icon)
            popupMenu.menuInflater.inflate(R.menu.student_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.home -> {
                        activity.startActivity(Intent(activity, StudenthomeActivity::class.java))
                        true
                    }
                    R.id.myProfile -> {
                        val sharedPref = activity.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                        val studentId = sharedPref.getInt("studentid", -1)

                        if (studentId != -1) {
                            val intent = Intent(activity, StudentprofileActivity::class.java)
                            intent.putExtra("studentid", studentId)
                            activity.startActivity(intent)
                        } else {
                            Toast.makeText(activity, "Student ID not found", Toast.LENGTH_SHORT).show()
                        }
                        true
                    }
                    R.id.overallduelist -> {
                        activity.startActivity(Intent(activity, StudentduelistActivity::class.java))
                        true
                    }
                    R.id.historyandreceipts -> {
                        activity.startActivity(Intent(activity, PaymenthistoryActivity::class.java))
                        true
                    }
                    R.id.splitter -> {
                        activity.startActivity(Intent(activity, SplitoptionsActivity::class.java))
                        true
                    }
                    R.id.buspass -> {
                        activity.startActivity(Intent(activity, StudentbuspassActivity::class.java))
                        true
                    }
                    R.id.mybuspass -> {
                        val sharedPref = activity.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                        val studentId = sharedPref.getInt("studentid", -1)

                        if (studentId != -1) {
                            val intent = Intent(activity, MybuspassActivity::class.java)
                            intent.putExtra("studentid", studentId)
                            // Clear top to prevent multiple instances
                            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                            activity.startActivity(intent)
                        } else {
                            Toast.makeText(activity, "Student ID not found", Toast.LENGTH_SHORT).show()
                        }
                        true
                    }

                    R.id.contactus -> {
                        activity.startActivity(Intent(activity, StudenthelpActivity::class.java))
                        true
                    }
                    R.id.logout -> {
                        activity.startActivity(Intent(activity, MainActivity::class.java))
                        Toast.makeText(activity, "Logged Out", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }
}
