package com.ix.dm.stepcounter.database

import androidx.room.Database
import androidx.room.RoomDatabase

//The data that is going to be used, comes from the Class, "User"
@Database(entities = [User::class], version = 1)

abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

}