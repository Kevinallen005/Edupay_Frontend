package com.example.feepayment
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.feepayment.adapter.StudentListAdapter
import com.example.feepayment.responses.DBstudentsResponse
import com.example.feepayment.responses.StudentData
import com.example.feepayment.retrofit.retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DatabasestudsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StudentListAdapter
    private var allStudents: List<StudentData> = listOf()
    private var classNumber: Int = -1

    private lateinit var imposeFeeButton: Button // ✅ new button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_databasestuds)

        val menuIcon = findViewById<ImageView>(R.id.menuIcon)
        AdminMenuHandler.setupMenu(this, menuIcon)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recycle)
        recyclerView.layoutManager = LinearLayoutManager(this)

        imposeFeeButton = findViewById(R.id.imposeFeeButton)
        imposeFeeButton.visibility = View.GONE // Hide initially

        val selectAllCheckBox = findViewById<CheckBox>(R.id.selectAllCheckBox)
        val filterButton = findViewById<Button>(R.id.filterButton)

        selectAllCheckBox.setOnCheckedChangeListener { _, isChecked ->
            adapter.selectAll(isChecked)
        }

        filterButton.setOnClickListener {
            val wrapper = ContextThemeWrapper(this, R.style.CustomPopupMenu)
            val popup = PopupMenu(wrapper, filterButton, 0, 0, R.style.CustomPopupMenu)
            popup.menuInflater.inflate(R.menu.filter_menu, popup.menu)

            try {
                val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
                fieldMPopup.isAccessible = true
                val mPopup = fieldMPopup.get(popup)
                mPopup.javaClass
                    .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(mPopup, true)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            popup.setOnMenuItemClickListener { item ->
                val titleStr = item.title.toString()
                filterButton.text = titleStr.take(6) + if (titleStr.length > 6) "..." else ""
                when (item.itemId) {
                    R.id.all -> adapter.filterByStatus("all")
                    R.id.paid -> adapter.filterByStatus("paid")
                    R.id.due -> adapter.filterByStatus("due")
                    R.id.sort_asc -> adapter.sortById(true)
                    R.id.sort_desc -> adapter.sortById(false)
                }
                true
            }

            popup.show()
        }

        imposeFeeButton.setOnClickListener {
            val selectedIds = adapter.getSelectedStudentIds()
            if (selectedIds.isNotEmpty()) {
                Log.d("DatabasestudsActivity", "Passing IDs: $selectedIds")
                val intent = Intent(this, ImposefeeActivity::class.java)
                intent.putIntegerArrayListExtra("selectedStudentIds", ArrayList(selectedIds))
                intent.putExtra("classNumber", classNumber)
                startActivity(intent)
            } else {
                Toast.makeText(this, "No students selected", Toast.LENGTH_SHORT).show()
            }
        }

        classNumber = intent.getIntExtra("classNumber", -1)
        if (classNumber != -1) {
            fetchStudents(classNumber)
        }
    }

    override fun onResume() {
        super.onResume()
        if (classNumber != -1) {
            fetchStudents(classNumber)
        }
    }

    private fun fetchStudents(classNumber: Int) {
        retrofit.instance.getStudentsByClass(classNumber)
            .enqueue(object : Callback<DBstudentsResponse> {
                override fun onResponse(
                    call: Call<DBstudentsResponse>,
                    response: Response<DBstudentsResponse>
                ) {
                    if (response.isSuccessful && response.body()?.status == "success") {
                        allStudents = response.body()?.data ?: emptyList()
                        adapter = StudentListAdapter(
                            allStudents,
                            { student -> showStudentOptionsDialog(student) },
                            { selectedList ->
                                imposeFeeButton.visibility =
                                    if (selectedList.size > 1) View.VISIBLE else View.GONE
                            }
                        )
                        recyclerView.adapter = adapter
                    } else {
                        Toast.makeText(
                            this@DatabasestudsActivity,
                            "Failed to load students",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<DBstudentsResponse>, t: Throwable) {
                    Toast.makeText(
                        this@DatabasestudsActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun showStudentOptionsDialog(student: StudentData) {
        val dialogView = LayoutInflater.from(this)
            .inflate(R.layout.studentoptionstoggle, null, false)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(true)

        dialogView.findViewById<TextView>(R.id.studentprofile)
            .setOnClickListener {
                val intent = Intent(this, AdminstudentprofileActivity::class.java)
                intent.putExtra("studentId", student.studentid)
                intent.putExtra("classNumber", classNumber)
                startActivity(intent)
                dialog.dismiss()
            }

        dialogView.findViewById<TextView>(R.id.imposefee)
            .setOnClickListener {
                val intent = Intent(this, ImposefeeActivity::class.java)
                intent.putExtra("classNumber", classNumber)
                intent.putIntegerArrayListExtra("selectedStudentIds", arrayListOf(student.studentid))
                startActivity(intent)
                dialog.dismiss()
            }

        dialogView.findViewById<TextView>(R.id.scholarship)
            .setOnClickListener {
                val intent = Intent(this, ScholarshipActivity::class.java)
                intent.putExtra("classNumber", classNumber)
                intent.putExtra("studentId", student.studentid)
                startActivity(intent)
                dialog.dismiss()
            }

//        dialogView.findViewById<TextView>(R.id.removestudent)
//            .setOnClickListener {
//                // Remove student logic
//                dialog.dismiss()
//            }

        dialog.show()
    }
}
