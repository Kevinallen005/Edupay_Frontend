package com.simats.feepayment.responses

data class PaymentHistoryResponse(
    val status: String,
    val data: List<PaymentHistoryItem>?
)

data class PaymentHistoryItem(
    val feename: String,
    val paydate: String,
    val feeamt: Int
)
