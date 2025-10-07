package com.example.feepayment.responses

data class LoginResponse(
    val status: String,
    val message: String,
    val user: User?
)

data class User(
    val studentid: Int,
    val username: String,
    val name: String,
    val email: String,
    val role : String
)
