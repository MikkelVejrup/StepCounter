package com.ix.dm.stepcounter.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface UserDao {
    @get:Query("SELECT * FROM StepTable where uid = 1")
    val user: User?

    @Insert //Inserting/Creating new user/day
    fun insert(user: User)

    //Counts all users in Database
    @Query("SELECT COUNT(*) from StepTable")
    fun countUsers(): Int

    //Gets all users/Days stored
    @Query("SELECT * from StepTable")
    fun getAllDays(): List<User>

    //Gets specific user from Database
    @Query("SELECT * from StepTable WHERE dayCode = :dayCode")
    fun getSpecificUser(dayCode: String): User

    //Gets specific user stepsTaken
    @Query("SELECT * from StepTable WHERE dayCode = :dayCode")
    fun getSpecificUserSteps(dayCode: String): Float

    //Deletes all days in Database
    @Query("DELETE from StepTable")
    fun deleteAll()

    //=== Updating user variables ==========================================================
    @Update //Updates everything in the user
    fun update(user: User?)

    //Udpdates only the steps counted in a specific day
    @Query("UPDATE StepTable SET stepsCounted=:steps WHERE dayCode=:day")
    fun updateStepsCounted(steps: Int, day: String)

    //Updates only the daily step Goal in a specific day
    @Query("UPDATE StepTable SET stepDayGoal=:stepGoal WHERE dayCode=:day")
    fun updateStepDayGoal(stepGoal: Int, day: String)
    //======================================================================================


}
