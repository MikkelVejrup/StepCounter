package com.ix.dm.stepcounter.database

import androidx.room.*

@Dao
interface UserDao {
    @Insert
    fun insert(user: User)

    @Query("SELECT * from StepTable")
    fun getAllDays(): List<User>

    @Query("DELETE from StepTable")
    fun deleteAll()

}
