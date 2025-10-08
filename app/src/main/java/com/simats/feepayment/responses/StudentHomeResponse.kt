package com.simats.feepayment.responses

import com.google.gson.annotations.SerializedName
data class StudentHomeResponse(
    val status: String,
    val profile: Profile?,
    val message: String? = null
)

data class Profile(
    val name: String,
    @SerializedName("class")
    val class_: Int,
    val sec: String,
    val photo: String,
    val incharge: String
)