package com.iremeber.rememberfriends.domain.update_entities

data class UpdateScheduleAlarmModel(
    val newTimeInMillis: Long,
    val requestCode: Int,
    val interval: Int
)
