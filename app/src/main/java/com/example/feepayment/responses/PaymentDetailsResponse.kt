package com.example.feepayment.responses


data class PaymentDetailsResponse(
    val status: String,
    val data: PaymentDetails
)

data class PaymentDetails(
    val name: String,
    val feename: String,
    val feeamt: Int,
    val duedate: String
)
