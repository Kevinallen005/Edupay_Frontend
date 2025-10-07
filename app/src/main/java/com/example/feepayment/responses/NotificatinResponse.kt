package com.example.feepayment.responses

data class NotificationResponse(
    val status: String,
    val notifications: List<NotificationItem>
)
data class NotificationItem(
    val title: String,
    val message: String
)


