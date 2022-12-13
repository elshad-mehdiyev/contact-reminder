package com.iremeber.rememberfriends.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ScheduleAlarmModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val timInMillis: Long,
    val requestCode: Int,
    val message: String,
    val interval: Int,
    val updateRequestCode: Int
)
