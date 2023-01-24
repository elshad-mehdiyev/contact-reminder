package com.iremeber.rememberfriends.data.models.db_entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FavoriteContactModel(
    @PrimaryKey(autoGenerate = true)
    val dbId: Int? = null,
    val id: String,
    val name: String,
    val date: String,
    val interval: String,
    val requestCode: Int,
    val firstLetter: String,
    val dateMessage: String,
    val intervalMessage: String,
    val startHour: String
)