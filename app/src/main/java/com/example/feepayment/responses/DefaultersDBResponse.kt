package com.example.feepayment.responses


data class DefaultersDBResponse(
    val status: String,
    val `class`: String,
    val data: List<StudentData2>
)

data class StudentData2(
    val studentid: Int,
    val name: String,
    val `class`: Int,
    val sec: String,
    val status: String
)
