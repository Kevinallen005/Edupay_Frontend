package com.example.feepayment.responses

import com.google.gson.annotations.SerializedName

data class FeeItem(
    val feename: String,
    val feeamt: Int
)

data class FeesDueResponse(
    val status: String,
    val data: List<FeeItem>
)
