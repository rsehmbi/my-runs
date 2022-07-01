package com.example.ramanpreet_sehmbi

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.ramanpreet_sehmbi.CustomAdapters.HistoryListAdapter
import com.example.ramanpreet_sehmbi.Database.*
import com.example.ramanpreet_sehmbi.UIHelpers.convertTypeIntToString
import com.example.ramanpreet_sehmbi.ViewModels.UnitViewModel


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
    private lateinit var historyView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        historyView = inflater.inflate(R.layout.fragment_history, container, false)
        return historyView
    }

    fun refreshFragment(activity: FragmentActivity){
        database = ExerciseEntryDatabase.getInstance(activity)
        databaseDao = database.exerciseEntryDatabaseDao
        repository = ExerciseEntryRepository(databaseDao)
        exerciseFactory = ExerciseEntryViewModelFactory(repository)
        exerciseEntryViewModel = ViewModelProvider(activity, exerciseFactory).get(ExerciseEntryViewModel::class.java)

        var unitViewModel = ViewModelProvider(activity)[UnitViewModel::class.java]
        println("The unit is"+ unitViewModel.UNITS)
        println("The modified unit is"+ unitViewModel.UNITS)

        exerciseEntryViewModel.allExerciseEntriesLiveData.observe(activity){
            println("Do i come here")
            for (entry in it){
                if (!id.contains(entry.id.toString())){
                    id.add(entry.id.toString())
                    entryType.add(convertTypeIntToString(entry.inputType.toString()))
                    activityType.add(entry.activityType)
                    datetime.add(entry.dateTime)
                    distance.add(entry.distance.toString())
                    duration.add(entry.duration.toString())
                }
            }
            val myListAdapter = HistoryListAdapter(activity,
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
                val clickedItemId = adapterView.getItemAtPosition(position)
                val itemIdAtPos = adapterView.getItemIdAtPosition(position)

                val currentEntryID: String = clickedItemId.toString()
                // Bug could be here
                val intent = Intent(activity, ShowSingleEntry::class.java)
                intent.putExtra("EXERCISE_ENTRY_ID", currentEntryID)
                startActivity(intent)

                println("Click on item at $clickedItemId its item id $itemIdAtPos")
            }
        }
        Toast.makeText(activity, "I am in Hello", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        Toast.makeText(requireContext(), "onResume", Toast.LENGTH_SHORT).show()
    }


}