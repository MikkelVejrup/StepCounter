package com.ix.dm.stepcounter.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "StepTable")
data class User(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    val dayCode: String = "",
    val stepsCounted: Int = 0,
    val stepDayGoal: Int = 0
)
