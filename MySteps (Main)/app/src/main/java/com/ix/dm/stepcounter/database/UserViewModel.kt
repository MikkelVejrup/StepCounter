package com.ix.dm.stepcounter.database

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log.d
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application): AndroidViewModel(application) {

    val getAllDays: LiveData<List<User>>
    private val repository: UserRepository

    //used when UserViewModel is called
    init{
        val userDao = UserDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
        getAllDays = repository.getAllDays

        if (countUsers() == 0) {
            d("UserLogDEBUG","trying to initialize DB!---------")
            initializeDatabaseStorage()
            d("UserLogDEBUG","DB was initialized!?----------")
        } else {
            d("UserLogDEBUG","DB already initialized")
        }
    }

    fun insert(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(user)
        }
    }

    private fun countUsers(): Int {
        return repository.countUsers()
    }

    private fun getSpecificUser(daycode: String): User {
        return repository.getSpecificUser(daycode)
    }

    private fun getSpecificUserDayCode(uid: Int): String {
        return repository.getSpecificUserDayCode(uid)
    }

    private fun getSpecificUserID(daycode: String): Int {
        return repository.getSpecificUserID(daycode)
    }

    private fun initializeDatabaseStorage() {
        //This function should create every user/day that can hold a value
        val dayCodeArray = arrayOf<String>("Mo","Tu","We","Th","Fr","Sa","Su")

        var i = 0
        for (days in dayCodeArray.withIndex()) {
            val uid = 0 //Room DB will generate auto ID Increment after first one
            val dayCode: String = dayCodeArray[i]
            val stepsCounted: Int = 0
            val stepDayGoal: Int = 2500 //Default value stepGoal

            val tempUser = User(uid, dayCode, stepsCounted, stepDayGoal)

            insert(tempUser)
            //d("UserLogDEBUG","inserted user nr. = ${i}")

            //logging inserted user (MIGHT BE DELETED LATER)
            val currentDay = getSpecificUserDayCode(uid = i)
            val currentDay2 = getSpecificUserID(daycode = dayCodeArray[i])
            d("UserLogDEBUG","Day inserted was: ${currentDay2} - inserted at $i")

            i++

        }
    }
}