package com.example.ramanpreet_sehmbi.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseEntryDatabaseDao {

    @Insert
    suspend fun insertInputType(exerciseEntry: ExerciseEntry)

    @Query("SELECT * FROM exercise_entry_table")
    fun getAllExericiseEntries(): Flow<MutableList<ExerciseEntry>>

    @Query("DELETE FROM exercise_entry_table")
    suspend fun deleteAll()

    @Query("UPDATE exercise_entry_table set metric_info= :metric WHERE id= :key")
    suspend fun updateMetric(metric: String, key:Long)

    @Query("DELETE FROM exercise_entry_table WHERE id= :key")
    suspend fun deleteExerciseEntry(key: Long)


    @Query("SELECT * FROM exercise_entry_table WHERE id= :key")
    fun getExerciseEntry(key: Long): LiveData<ExerciseEntry>
}