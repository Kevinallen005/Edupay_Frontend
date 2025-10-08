package com.simats.feepayment.responses


data class StudentCountResponse(
    val status: String,
    val data: Map<String, Int>
)
