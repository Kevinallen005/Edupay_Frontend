package com.simats.feepayment.responses

data class FeeItem(
    val feename: String,
    val feeamt: Int
)

data class FeesDueResponse(
    val status: String,
    val data: List<FeeItem>
)
