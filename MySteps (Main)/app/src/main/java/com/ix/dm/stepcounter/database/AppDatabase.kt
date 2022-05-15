package com.ix.dm.stepcounter.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//Tutorial to get it working
// https://www.youtube.com/watch?v=lwAvI3WDXBY&list=PLSrm9z4zp4mEPOfZNV9O-crOhoMa0G2-o&index=1&ab_channel=Stevdza-San


//The data that is going to be used, comes from the Class, "User"
@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
/*
    companion object {
        // Singleton to prevent multiple instances from existing
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getAppDatabase(context: Context): AppDatabase? {
            //If instance already exists, return it
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }


            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "Step_Database"
                ).build()
                INSTANCE = instance
            }

            return INSTANCE
        }
    }
    */

}