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

    fun getSpecificUserByDay(daycode: String): User {
        return userDao.getSpecificUserByDay(daycode)
    }

    fun getSpecificUserById(uid: Int): User {
        return userDao.getSpecificUserById(uid)
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

    fun updateAllStepDayGoal(stepGoal: Int) {
        userDao.updateAllStepDayGoal((stepGoal))
    }

}