package com.example.feepayment.responses


data class DBstudentsResponse(
    val status: String,
    val `class`: Int,
    val data: List<StudentData>
)

data class StudentData(
    val studentid: Int,
    val name: String,
    val `class`: Int,
    val sec: String,
    val status: String
)
