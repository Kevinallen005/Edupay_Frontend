package com.simats.feepayment.responses


data class InchargeItem(
    val incharge: String
)

data class InchargeResponse(
    val status: String,
    val data: List<InchargeItem>
)
