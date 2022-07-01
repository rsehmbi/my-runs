package com.example.ramanpreet_sehmbi.Database

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ExerciseEntryViewModel(private val repository: ExerciseEntryRepository) : ViewModel() {

    val allExerciseEntriesLiveData: LiveData<List<ExerciseEntry>> =
        repository.allExerciseEntries.asLiveData()

    fun insert(exerciseEntry: ExerciseEntry) {
        repository.insert(exerciseEntry)
    }

    fun deleteFirst() {
        val exerciseList = allExerciseEntriesLiveData.value
        if (exerciseList != null && exerciseList.size > 0) {
            val id = exerciseList[0].id
            repository.delete(id)
        }
    }

    fun deleteAll() {
        val exerciseList = allExerciseEntriesLiveData.value
        if (exerciseList != null && exerciseList.size > 0)
            repository.deleteAll()
    }

}

class ExerciseEntryViewModelFactory(private val repository: ExerciseEntryRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExerciseEntryViewModel::class.java))
            return ExerciseEntryViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}