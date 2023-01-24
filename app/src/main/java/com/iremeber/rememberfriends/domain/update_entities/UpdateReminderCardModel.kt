package com.iremeber.rememberfriends.domain.update_entities

data class UpdateReminderCardModel(
    val date: String,
    val interval: String,
    val beginHour: String,
    val dateMessage: String,
    val intervalMessage: String,
    val requestCode: Int
)
