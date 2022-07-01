package com.example.ramanpreet_sehmbi

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.ramanpreet_sehmbi.Database.*
import com.example.ramanpreet_sehmbi.UIHelpers.convertTypeIntToString

class ShowSingleEntry : AppCompatActivity() {
    var entryId: String = ""
    private lateinit var database: ExerciseEntryDatabase
    private lateinit var databaseDao: ExerciseEntryDatabaseDao
    private lateinit var repository: ExerciseEntryRepository
    private lateinit var exerciseEntryViewModel: ExerciseEntryViewModel
    private lateinit var exerciseFactory: ExerciseEntryViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_single_entry)

        val inputEditText = findViewById<EditText>(R.id.input_type_id)
        val AcitivityEditText = findViewById<EditText>(R.id.activity_type_id)
        val dateTimeEditText = findViewById<EditText>(R.id.date_and_time_id)
        val durationEditText = findViewById<EditText>(R.id.duration_id)
        val distanceEditText = findViewById<EditText>(R.id.distance_id)
        val caloriesEditText = findViewById<EditText>(R.id.calories_id)

        database = ExerciseEntryDatabase.getInstance(this)
        databaseDao = database.exerciseEntryDatabaseDao
        repository = ExerciseEntryRepository(databaseDao)
        exerciseFactory = ExerciseEntryViewModelFactory(repository)
        exerciseEntryViewModel = ViewModelProvider(this, exerciseFactory).get(ExerciseEntryViewModel::class.java)
        exerciseEntryViewModel.allExerciseEntriesLiveData.observe(this){
            val extras = intent.extras
            if (extras != null) {
                entryId = extras.getString("EXERCISE_ENTRY_ID").toString()
            }
            Toast.makeText(this, "The entry id" + entryId, Toast.LENGTH_SHORT).show()
            for (entry in it) {
                if(entry.id.toString() == entryId){
                    inputEditText.setText(convertTypeIntToString(entry.inputType.toString()))
                    AcitivityEditText.setText(entry.activityType)
                    dateTimeEditText.setText(entry.dateTime)
                    durationEditText.setText(entry.duration.toString())
                    distanceEditText.setText(entry.distance.toString())
                    caloriesEditText.setText(entry.calorie.toString())
                }
            }
        }
        inputEditText.setEnabled(false);
        AcitivityEditText.setEnabled(false);
        dateTimeEditText.setEnabled(false);
        durationEditText.setEnabled(false);
        distanceEditText.setEnabled(false);
        caloriesEditText.setEnabled(false);
    }
}