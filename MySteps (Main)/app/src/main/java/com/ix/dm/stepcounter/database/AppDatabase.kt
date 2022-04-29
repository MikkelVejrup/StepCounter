package com.ix.dm.stepcounter.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    //TESTVARIABLE TIL GIT KRAKEN
    private var testvar = 0

}