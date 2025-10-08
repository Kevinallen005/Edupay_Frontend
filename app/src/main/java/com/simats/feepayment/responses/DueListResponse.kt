package com.simats.feepayment.responses

data class DueListResponse(
    val status: String,
    val data: List<DueItem>?
)

data class DueItem(
    val feename: String,
    val feeamt: Int,
    val duedate: String
)
