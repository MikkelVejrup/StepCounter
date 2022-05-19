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
            d("UserLogDEBUG","Initializing DB!--------------")
            initializeDatabaseStorage()
            d("UserLogDEBUG","DB was initialized!-----------")
        } else {
            d("UserLogDEBUG","DB already initialized")
        }
    }

    fun insert(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(user)
        }
    }

    fun countUsers(): Int {
        return repository.countUsers()
    }

    fun update(user: User?) {
        repository.update(user)
    }

    fun getSpecificUserById(uid: Int): User {
        return repository.getSpecificUserById(uid)
    }

    fun getSpecificUserByDay(daycode: String): User {
        return repository.getSpecificUserByDay(daycode)
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

            i++
        }
    }
}