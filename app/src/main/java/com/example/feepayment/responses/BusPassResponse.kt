package com.example.feepayment.responses

import com.google.gson.annotations.SerializedName

data class BusPassResponse(
    val success: Boolean,
    val data: BusPassData?
)

data class BusPassData(
    val studentid: String,
    val name: String,
    val photo: String,
    val routename: String,
    @SerializedName("boarding_point")
    val boardingPoint: String,
    val amount: String,
    val status: String,
    @SerializedName("valid_from")
    val validFrom: String,
    @SerializedName("valid_until")
    val validUntil: String,
    val via: String
)
