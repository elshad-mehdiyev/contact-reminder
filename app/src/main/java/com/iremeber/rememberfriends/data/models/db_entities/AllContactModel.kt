package com.iremeber.rememberfriends.data.models.db_entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AllContactModel(
    @PrimaryKey(autoGenerate = true)
    val dbId: Int? = null,
    val id: String,
    var name: String,
    val firstLetter: String,
    val phoneNumber: String
)
