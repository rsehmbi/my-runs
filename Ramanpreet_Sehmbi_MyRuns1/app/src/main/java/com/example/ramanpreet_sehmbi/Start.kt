package com.example.ramanpreet_sehmbi

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ramanpreet_sehmbi.Database.*


class Start : Fragment() {

    lateinit var inputTypeSpinner: Spinner
    lateinit var activityTypeSpinner: Spinner
    lateinit var startButton: Button

    var INPUTTYPE = "Manual Entry"
    var INPUTTYPEPOSITION = 0

    var ACTIVITYTYPE = ""

    lateinit var saveButton: Button
    private lateinit var database: ExerciseEntryDatabase
    private lateinit var databaseDao: ExerciseEntryDatabaseDao
    private lateinit var repository: ExerciseEntryRepository
    private lateinit var exerciseEntryViewModel: ExerciseEntryViewModel
    private lateinit var exerciseFactory: ExerciseEntryViewModelFactory


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentStartView = inflater.inflate(R.layout.fragment_start, container, false)

        inputTypeSpinner = fragmentStartView.findViewById(R.id.input_type_id)
        val inputTypeAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.input_types,
            android.R.layout.simple_list_item_1
        );
        inputTypeSpinner.adapter = inputTypeAdapter
        inputTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                INPUTTYPE = "Manual Entry"
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val type = parent?.getItemAtPosition(position).toString()
                INPUTTYPE = type
                INPUTTYPEPOSITION = position
            }

        }

        activityTypeSpinner = fragmentStartView.findViewById(R.id.activity_types_id)
        val activityTypeAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.activity_types,
            android.R.layout.simple_list_item_1
        );
        activityTypeSpinner.adapter = activityTypeAdapter
        activityTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //TODO: Requirement not specified yet.
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val activitytype = parent?.getItemAtPosition(position).toString()
                ACTIVITYTYPE = activitytype
            }
        }

        startButton = fragmentStartView.findViewById(R.id.start_button_id)
        startButton.setOnClickListener() {
            if (INPUTTYPE == "Manual Entry") {
                val manualEntryIntent = Intent(activity, ManualEntry::class.java)
                manualEntryIntent.putExtra("INPUT_TYPE", INPUTTYPE)
                manualEntryIntent.putExtra("INPUT_TYPE_POSITION", INPUTTYPEPOSITION)
                manualEntryIntent.putExtra("ACTIVITY_TYPE", ACTIVITYTYPE)
                startActivity(manualEntryIntent)
            } else {
                startActivity(Intent(activity, GPS::class.java))
            }
        }

        // Database test
        database = ExerciseEntryDatabase.getInstance(requireActivity())
        databaseDao = database.exerciseEntryDatabaseDao
        repository = ExerciseEntryRepository(databaseDao)
        exerciseFactory = ExerciseEntryViewModelFactory(repository)
        exerciseEntryViewModel =
            ViewModelProvider(this, exerciseFactory).get(ExerciseEntryViewModel::class.java)

        exerciseEntryViewModel.allExerciseEntriesLiveData.observe(requireActivity()) {
            // Update user interface
            println("debug: ${it.size}")
            for (item in it) {
                println("The item id is " + item.id)
                println("The calorie is " + item.calorie)
                println("The comment is " + item.comment)
                println("The dateTime is " + item.dateTime)
            }
        }

        saveButton = fragmentStartView.findViewById(R.id.save_button_id)
        saveButton.setOnClickListener() {
            val exerciseEntryObj = ExerciseEntry()
            exerciseEntryObj.calorie = 150f
            exerciseEntryObj.comment = "testComment"
            exerciseEntryObj.dateTime = "8-09-21"
            exerciseEntryViewModel.insert(exerciseEntryObj)
        }

        return fragmentStartView
    }

}