package com.example.ramanpreet_sehmbi.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ExerciseEntry::class], version = 1)
abstract class ExerciseEntryDatabase : RoomDatabase() {
    abstract val exerciseEntryDatabaseDao: ExerciseEntryDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: ExerciseEntryDatabase? = null

        fun getInstance(context: Context): ExerciseEntryDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ExerciseEntryDatabase::class.java,
                        "exercise_entry_db"
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}