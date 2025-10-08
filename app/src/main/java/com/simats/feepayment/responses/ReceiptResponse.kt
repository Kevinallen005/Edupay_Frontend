package com.simats.feepayment.responses

import com.google.gson.annotations.SerializedName

data class ReceiptResponse(
    val name: String,
    @SerializedName("class")
    val class_: String,
    val sec: String,
    val feename: String,
    val feeamt: Int,
    val paydate: String,
    val duedate: String,
    val referenceid: String,
    val remarks: String?,
    val ScholarshipAmount: String
)
