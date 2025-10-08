package com.simats.feepayment.responses

data class ImposefeeResponse(
    val status: String,
    val data: List<StudentInfo>?
)

data class StudentInfo(
    val studentid: Int,
    val name: String
)