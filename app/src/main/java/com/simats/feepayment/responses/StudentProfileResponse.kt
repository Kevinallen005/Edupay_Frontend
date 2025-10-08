package com.simats.feepayment.responses

import com.google.gson.annotations.SerializedName

data class StudentProfileResponse(
    val studentid: Int,
    val username: String,
    val name: String,

    @SerializedName("class")
    val class_: String,

    val sec: String,
    val email: String,
    val bloodgroup: String,
    val photo: String,
    val fathername: String,
    val fatherno: String,
    val mothername: String,
    val motherno: String,
    val incharge: String?,
    val inchargeno: String?
)
