package com.simats.feepayment.responses

data class AdminHomeResponse(
    val school_name: String,
    val fee: FeeDetails,
    val total_students: Int,
    val status: String
)

data class FeeDetails(
    val imposed: Int,
    val collected: Int,
    val scholarship: Int,
    val due: Int
)