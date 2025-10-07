package com.example.feepayment.responses


data class BusResponse(
    val status: String,
    val data: List<BusData>
)

data class BusData(
    val routename: String,
    val type: String,
    val via: String,
    val amount: Int,
    val km:Int,
    val seats:Int
)
