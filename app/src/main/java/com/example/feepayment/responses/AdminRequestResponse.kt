package com.example.feepayment.responses

data class AdminRequestResponse(
    val status: String,
    val data: List<AdminRequestData>?
)

data class AdminRequestData(
    val studentid: Int,
    val routename: String,
    val amount: Int,
    var status: String,
    val via: String,
    val boarding_point: String
)
