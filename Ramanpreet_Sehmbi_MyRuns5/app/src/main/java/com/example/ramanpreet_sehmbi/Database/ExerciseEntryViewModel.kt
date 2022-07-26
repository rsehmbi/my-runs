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

    val allExerciseEntriesLiveData: LiveData<MutableList<ExerciseEntry>> =
        repository.allExerciseEntries.asLiveData()

    fun insert(exerciseEntry: ExerciseEntry) {
        repository.insert(exerciseEntry)
    }

    fun getEntry(id: Long): LiveData<ExerciseEntry> {
        return repository.getExerciseEntry(id)
    }

    fun deleteFirst(id: Long) {
        val exerciseList = allExerciseEntriesLiveData.value
        var entryObj: ExerciseEntry? = null
        if (exerciseList != null) {
            for (entry in exerciseList){
                if(entry.id == id){
                    entryObj = entry
                }
            }

        }
        if (entryObj != null) {
            exerciseList?.remove(entryObj)
        }
        if (exerciseList != null) {
            repository.delete(id)
        }
    }

    fun updateMetric(metric: String, key:Long) {
        val exerciseList = allExerciseEntriesLiveData.value
        if (exerciseList != null && exerciseList.size > 0)
            repository.updateMetric(metric, key)
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