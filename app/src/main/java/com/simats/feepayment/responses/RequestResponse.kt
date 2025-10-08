package com.simats.feepayment.responses

data class RequestResponse(
    val status: String,
    val data: List<RequestData>?,   // nullable now
    val message: String?
)

data class RequestData(
    val routename: String,
    val amount: Int,
    val status: String,
    val via: String,
    val boarding_point: String

)
