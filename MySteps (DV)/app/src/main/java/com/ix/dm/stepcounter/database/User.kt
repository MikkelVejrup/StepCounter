package com.ix.dm.stepcounter.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey
    val uid: Int,
    val name: String,
    val address: String,
    val dateOfBirth: Long = 0
)