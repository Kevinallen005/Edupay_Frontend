package com.simats.feepayment.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.simats.feepayment.R
import com.simats.feepayment.responses.StudentData

class StudentListAdapter(
    private var students: List<StudentData>,
    private val onMoreOptionsClick: (StudentData) -> Unit,
    private val onSelectionChanged: (List<Int>) -> Unit // ✅ callback for selection updates
) : RecyclerView.Adapter<StudentListAdapter.StudentViewHolder>() {

    private var allStudents: List<StudentData> = students
    private var selectedStates = BooleanArray(students.size)

    inner class StudentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.studentName)
        val id: TextView = view.findViewById(R.id.studentId)
        val classInfo: TextView = view.findViewById(R.id.studentClass)
        val status: TextView = view.findViewById(R.id.statusBadge)
        val moreIcon: ImageView = view.findViewById(R.id.moreOptions)
        val checkBox: CheckBox = view.findViewById(R.id.checkBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dbstudslist, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]
        holder.name.text = student.name
        holder.id.text = "ID: ${student.studentid}"
        holder.classInfo.text = "${student.`class`}-${student.sec}"

        if (student.status.equals("paid", ignoreCase = true)) {
            holder.status.text = "✅ Paid"
            holder.status.setTextColor(Color.parseColor("#388E3C"))
            holder.status.setBackgroundResource(R.drawable.curved_border_green)
        } else {
            holder.status.text = "⚠️ Due"
            holder.status.setTextColor(Color.parseColor("#D32F2F"))
            holder.status.setBackgroundResource(R.drawable.curved_border_lightyellow)
        }

        // Prevent recycling issues
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = selectedStates[position]

        // Handle checkbox selection
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            selectedStates[position] = isChecked
            onSelectionChanged(getSelectedStudentIds()) // ✅ update activity on change
        }

        // Handle "more options" click
        holder.moreIcon.setOnClickListener {
            onMoreOptionsClick(student)
        }
    }

    override fun getItemCount(): Int = students.size

    fun selectAll(isSelected: Boolean) {
        selectedStates.fill(isSelected)
        notifyDataSetChanged()
        onSelectionChanged(getSelectedStudentIds()) // ✅ notify activity
    }

    fun filterByStatus(status: String) {
        students = when (status.lowercase()) {
            "paid" -> allStudents.filter { it.status.equals("paid", true) }
            "due" -> allStudents.filter { !it.status.equals("paid", true) }
            else -> allStudents
        }
        selectedStates = BooleanArray(students.size)
        notifyDataSetChanged()
        onSelectionChanged(getSelectedStudentIds())
    }

    fun sortById(ascending: Boolean) {
        students = if (ascending) {
            students.sortedBy { it.studentid }
        } else {
            students.sortedByDescending { it.studentid }
        }
        notifyDataSetChanged()
    }

    fun setData(newList: List<StudentData>) {
        allStudents = newList
        students = newList
        selectedStates = BooleanArray(students.size)
        notifyDataSetChanged()
        onSelectionChanged(getSelectedStudentIds())
    }

    // ✅ Get IDs of selected students
    fun getSelectedStudentIds(): List<Int> {
        return students.mapIndexedNotNull { index, student ->
            if (selectedStates[index]) student.studentid else null
        }
    }
}
