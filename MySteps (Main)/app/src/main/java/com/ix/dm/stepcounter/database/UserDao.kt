package com.ix.dm.stepcounter.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface UserDao {
    @Insert
    fun insert(user: User)

    @Update //Updates everything in the user
    fun update(user: User?)

    //Udpdates only the steps counted in a specific day
    @Query("UPDATE StepTable SET stepsCounted=:steps WHERE dayCode = :day")
    fun update(steps: Float?, day: String)

    @Query("SELECT * from StepTable")
    fun getAllDays(): List<User>

    @Query("DELETE from StepTable")
    fun deleteAll()

}
