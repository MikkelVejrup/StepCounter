package com.ix.dm.stepcounter.database

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface UserDao {
    @get:Query("SELECT * FROM StepTable where uid = 1")
    val user: User?

    @Insert(onConflict = OnConflictStrategy.IGNORE) //Inserting/Creating new user/day
    suspend fun insert(user: User)

    //Counts all users in Database
    @Query("SELECT COUNT(*) FROM StepTable")
    fun countUsers(): Int

    //Deletes all days in Database
    @Query("DELETE FROM StepTable")
    fun deleteAll()

    //=== GETTERS ==========================================================================
    //Gets all users/Days stored
    @Query("SELECT * FROM StepTable")
    fun getAllDays(): LiveData<List<User>>

    //Gets specific user from Database
    @Query("SELECT * FROM StepTable WHERE dayCode = :dayCode")
    fun getSpecificUser(dayCode: String): User

    //Gets specific user stepsTaken
    @Query("SELECT * FROM StepTable WHERE dayCode = :dayCode")
    fun getSpecificUserSteps(dayCode: String): Float

    @Query("SELECT dayCode FROM StepTable WHERE uid = :uid")
    fun getSpecificUserDayCode(uid: Int): String

    @Query("SELECT uid FROM StepTable WHERE dayCode = :daycode")
    fun getSpecificUserID(daycode: String): Int
    //======================================================================================

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
