package com.ix.dm.stepcounter.database

import androidx.lifecycle.LiveData

class UserRepository(private val userDao: UserDao) {

    val getAllDays: LiveData<List<User>> = userDao.getAllDays()

    suspend fun insert(user: User) {
        userDao.insert(user)
    }

    fun countUsers(): Int {
        return userDao.countUsers()
    }

    fun getSpecificUser(daycode: String): User {
        return userDao.getSpecificUser(daycode)
    }

    fun getSpecificUserDayCode(uid: Int): String {
        return userDao.getSpecificUserDayCode(uid)
    }

    fun getSpecificUserID(daycode: String): Int {
        return userDao.getSpecificUserID(daycode)
    }

    fun getSpecificUserSteps(daycode: String): Float {
        return userDao.getSpecificUserSteps(daycode)
    }

    fun deleteAll() {
        userDao.deleteAll()
    }

    fun update(user: User?) {
        userDao.update(user)
    }

    fun updateStepsCounted(steps: Int, day: String) {
        userDao.updateStepsCounted(steps, day)
    }

    fun updateStepDayGoal(stepGoal: Int, day: String) {
        userDao.updateStepDayGoal(stepGoal, day)
    }

}