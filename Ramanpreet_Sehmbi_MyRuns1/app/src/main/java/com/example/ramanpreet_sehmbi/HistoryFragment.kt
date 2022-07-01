package com.example.ramanpreet_sehmbi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.ramanpreet_sehmbi.CustomAdapters.HistoryListAdapter
import com.example.ramanpreet_sehmbi.Database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HistoryFragment : Fragment() {
    val id: MutableList<String> = mutableListOf()
    val entryType: MutableList<String> = mutableListOf()
    val activityType: MutableList<String> = mutableListOf()
    val datetime: MutableList<String> = mutableListOf()
    val distance: MutableList<String> = mutableListOf()
    val duration: MutableList<String> = mutableListOf()

    private lateinit var database: ExerciseEntryDatabase
    private lateinit var databaseDao: ExerciseEntryDatabaseDao
    private lateinit var repository: ExerciseEntryRepository
    private lateinit var exerciseEntryViewModel: ExerciseEntryViewModel
    private lateinit var exerciseFactory: ExerciseEntryViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val historyView = inflater.inflate(R.layout.fragment_history, container, false)
        database = ExerciseEntryDatabase.getInstance(requireActivity())
        databaseDao = database.exerciseEntryDatabaseDao
        repository = ExerciseEntryRepository(databaseDao)
        exerciseFactory = ExerciseEntryViewModelFactory(repository)
        exerciseEntryViewModel = ViewModelProvider(this, exerciseFactory).get(ExerciseEntryViewModel::class.java)
        exerciseEntryViewModel.allExerciseEntriesLiveData.observe(requireActivity()){
            for (entry in it){
                if (!id.contains(entry.id.toString())){
                    id.add(entry.id.toString())
                    entryType.add(entry.inputType.toString())
                    activityType.add(entry.activityType)
                    datetime.add(entry.dateTime)
                    distance.add(entry.distance.toString())
                    duration.add(entry.duration.toString())
                }
            }
            val myListAdapter = HistoryListAdapter(requireActivity(),
                id,
                entryType,
                activityType,
                datetime,
                distance,
                duration
            )
            val listView = historyView.findViewById<ListView>(R.id.history_list_id)
            listView.adapter = myListAdapter
            listView.setOnItemClickListener(){adapterView, view, position, id ->
                val itemAtPos = adapterView.getItemAtPosition(position)
                val itemIdAtPos = adapterView.getItemIdAtPosition(position)
                Toast.makeText(requireContext(), "Click on item at $itemAtPos its item id $itemIdAtPos", Toast.LENGTH_LONG).show()
            }
        }
        return historyView
    }
}