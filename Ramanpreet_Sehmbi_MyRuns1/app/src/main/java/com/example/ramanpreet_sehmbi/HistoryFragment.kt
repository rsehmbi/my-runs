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
import com.example.ramanpreet_sehmbi.ViewModels.Observer
import com.example.ramanpreet_sehmbi.ViewModels.UnitViewModel
import java.lang.Long.parseLong


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

    private lateinit var observer: Observer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        historyView = inflater.inflate(R.layout.fragment_history, container, false)

        database = ExerciseEntryDatabase.getInstance(requireActivity())
        databaseDao = database.exerciseEntryDatabaseDao
        repository = ExerciseEntryRepository(databaseDao)
        exerciseFactory = ExerciseEntryViewModelFactory(repository)
        exerciseEntryViewModel =
            ViewModelProvider(requireActivity(), exerciseFactory).get(ExerciseEntryViewModel::class.java)

        observer = ViewModelProvider(requireActivity())[Observer::class.java]

        val sharedPref: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(requireActivity().getApplicationContext())
        val units = sharedPref.getString("units", "")
        val unitViewModel = ViewModelProvider(requireActivity())[UnitViewModel::class.java]
        if (units != null) {
            unitViewModel.UNITS = units
        }
        exerciseEntryViewModel.allExerciseEntriesLiveData.observe(requireActivity()) {
            id.clear()
            entryType.clear()
            activityType.clear()
            datetime.clear()
            distance.clear()
            duration.clear()
            for (entry in it) {
                id.add(entry.id.toString())
                entryType.add(convertTypeIntToString(entry.inputType.toString()))
                activityType.add(entry.activityType)
                datetime.add(entry.dateTime)
                distance.add(String.format("%.2f", entry.distance) )
                duration.add(entry.duration.toString())
            }
            val myListAdapter = HistoryListAdapter(
                requireActivity(),
                id,
                entryType,
                activityType,
                datetime,
                distance,
                duration,
                unitViewModel.UNITS,
            )
            var listView = historyView.findViewById<ListView>(R.id.history_list_id)
            listView.adapter = myListAdapter
            listView.setOnItemClickListener() { adapterView, view, position, id ->
                val clickedItemId = parseLong(adapterView.getItemAtPosition(position).toString())
                val itemIdAtPos = adapterView.getItemIdAtPosition(position)
                observer.observer = true
                exerciseEntryViewModel.getEntry(id=clickedItemId).observe(requireActivity()){
                    if (it != null && observer.observer){
                        observer.observer = false
                        Toast.makeText(requireContext(), "${convertTypeIntToString(it.inputType.toString())}", Toast.LENGTH_SHORT).show()
                        if (convertTypeIntToString(it.inputType.toString()) == "GPS") {
                            val currentEntryID: String = clickedItemId.toString()
                            val intent = Intent(activity, gpsentry::class.java)
                            intent.putExtra("EXERCISE_ENTRY_ID", currentEntryID)
                            startActivity(intent)
                        }
                        else if (convertTypeIntToString(it.inputType.toString()) == "Manual Entry"){
                            val currentEntryID: String = clickedItemId.toString()
                            val intent = Intent(activity, ShowSingleEntry::class.java)
                            intent.putExtra("EXERCISE_ENTRY_ID", currentEntryID)
                            startActivity(intent)
                        }
                        else if (convertTypeIntToString(it.inputType.toString()) == "Automatic"){
                            val currentEntryID: String = clickedItemId.toString()
                            val intent = Intent(activity, gpsentry::class.java)
                            intent.putExtra("EXERCISE_ENTRY_ID", currentEntryID)
                            startActivity(intent)
                        }
                    }
                }
                println("Click on item at $clickedItemId its item id $itemIdAtPos")
            }
        }
        return historyView
    }

    fun refreshFragment(activity: FragmentActivity) {
    }
}