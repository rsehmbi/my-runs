package com.example.ramanpreet_sehmbi.Database

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ExerciseEntryRepository(private val exerciseEntryDatabaseDao: ExerciseEntryDatabaseDao) {
    val allExerciseEntries: Flow<List<ExerciseEntry>> =
        exerciseEntryDatabaseDao.getAllExericiseEntries()


    fun insert(exerciseEntry: ExerciseEntry) {
        CoroutineScope(IO).launch {
            exerciseEntryDatabaseDao.insertInputType(exerciseEntry)
        }
    }

    fun delete(id: Long) {
        CoroutineScope(IO).launch {
            exerciseEntryDatabaseDao.deleteExerciseEntry(id)
        }
    }

    fun deleteAll() {
        CoroutineScope(IO).launch {
            exerciseEntryDatabaseDao.deleteAll()
        }
    }

}