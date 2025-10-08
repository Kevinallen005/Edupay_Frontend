package com.simats.feepayment

import android.app.Activity
import android.content.Intent
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast

object AdminMenuHandler {

    fun setupMenu(activity: Activity, icon: ImageView) {
        icon.setOnClickListener {
            val popupMenu = PopupMenu(activity, icon)
            popupMenu.menuInflater.inflate(R.menu.admin_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.Dashboard -> {
                        activity.startActivity(Intent(activity, AdminhomeActivity::class.java))
                        true
                    }
                    R.id.DatabaseManagement-> {
                        activity.startActivity(Intent(activity, MaindatabaseActivity::class.java))
                        true
                    }
                    R.id.Defaulters -> {
                        activity.startActivity(Intent(activity, DefaultersdbActivity::class.java))
                        true
                    }

                    R.id.BusReq -> {
                        activity.startActivity(Intent(activity, AdminbusrequestsActivity::class.java))
                        true
                    }

                    R.id.SupportCeter -> {
                        activity.startActivity(Intent(activity, AdmincontactusActivity::class.java))
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