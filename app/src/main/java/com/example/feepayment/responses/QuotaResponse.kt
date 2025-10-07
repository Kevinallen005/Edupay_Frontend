package com.example.feepayment.responses


data class QuotaItem(
    val quota: String,
    val percentage: String
)

data class QuotaResponse(
    val status: String,
    val data: List<QuotaItem>
)
