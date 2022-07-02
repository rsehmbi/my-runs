package com.example.ramanpreet_sehmbi

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.ramanpreet_sehmbi.CustomDialog.Companion.TITLE_KEY
import com.example.ramanpreet_sehmbi.Database.*
import com.example.ramanpreet_sehmbi.UIHelpers.convertMilesToKM
import com.example.ramanpreet_sehmbi.ViewModels.UnitViewModel



class ManualEntry : AppCompatActivity() {

    lateinit var manualEntries: ListView
    var INPUT_TYPE = ""
    var DATE_SELECTED = ""
    var TIME_SELECTED = ""
    var INPUT_TYPE_POSITION = -1
    var ACTIVITY_TYPE = ""
    var DURATION_ENTERED = ""
    var COMMENT_ENTERED = ""
    var HEARTRATE_ENTERED = ""
    var CALORIES_ENTERED = ""
    var DISTANCE_ENTERED = ""

    private lateinit var database: ExerciseEntryDatabase
    private lateinit var databaseDao: ExerciseEntryDatabaseDao
    private lateinit var repository: ExerciseEntryRepository
    private lateinit var exerciseEntryViewModel: ExerciseEntryViewModel
    private lateinit var exerciseFactory: ExerciseEntryViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_entry)
        setUpManualEntriesList()
        val extras = intent.extras
        if (extras != null) {
            INPUT_TYPE = extras.getString("INPUT_TYPE").toString()
            INPUT_TYPE_POSITION = extras.getInt("INPUT_TYPE_POSITION")
            ACTIVITY_TYPE = extras.getString("ACTIVITY_TYPE").toString()
        }
    }

    fun setUpManualEntriesList() {
        manualEntries = findViewById(R.id.manual_list_id)
        val manualEntriesAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.manual_list_items,
            android.R.layout.simple_list_item_1
        );
        manualEntries.adapter = manualEntriesAdapter
        manualEntries.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val selectedItemText = parent.getItemAtPosition(position).toString()
                if (selectedItemText == "Date") {
                    handleDateClicked()
                } else if (selectedItemText == "Time") {
                    handleTimeClicked()
                } else {
                    showPopUpDialog(selectedItemText)
                    handleDialogResult()
                }
            }
    }

    fun handleDialogResult() {
        supportFragmentManager.setFragmentResultListener(
            "CUSTOM_DIALOG_REQUEST_KEY",
            this
        ) { resultkey, bundle ->
            if (resultkey == "CUSTOM_DIALOG_REQUEST_KEY") {
                if (bundle.containsKey("DISTANCE_ENTERED")) {
                    DISTANCE_ENTERED = bundle.get("DISTANCE_ENTERED").toString()
                } else if (bundle.containsKey("DURATION_ENTERED")) {
                    DURATION_ENTERED = bundle.get("DURATION_ENTERED").toString()
                } else if (bundle.containsKey("CALORIES_ENTERED")) {
                    CALORIES_ENTERED = bundle.get("CALORIES_ENTERED").toString()
                } else if (bundle.containsKey("HEARTRATE_ENTERED")) {
                    HEARTRATE_ENTERED = bundle.get("HEARTRATE_ENTERED").toString()
                } else if (bundle.containsKey("COMMENT_ENTERED")) {
                    COMMENT_ENTERED = bundle.get("COMMENT_ENTERED").toString()
                }
            }
        }
    }

    fun handleDateClicked() {
        val datePickerFragment = com.example.ramanpreet_sehmbi.DatePicker()
        datePickerFragment.show(supportFragmentManager, null)
        supportFragmentManager.setFragmentResultListener(
            "DATE_REQUEST_KEY",
            this
        ) { resultkey, bundle ->
            if (resultkey == "DATE_REQUEST_KEY") {
                DATE_SELECTED = bundle.get("DATE_SELECTED").toString()
            }
        }
    }

    fun handleTimeClicked() {
        val timePickerFragment = com.example.ramanpreet_sehmbi.TimePicker()
        timePickerFragment.show(supportFragmentManager, null)
        supportFragmentManager.setFragmentResultListener(
            "TIME_REQUEST_KEY",
            this
        ) { resultkey, bundle ->
            if (resultkey == "TIME_REQUEST_KEY") {
                TIME_SELECTED = bundle.get("TIME_SELECTED").toString()
            }
        }
    }

    fun showPopUpDialog(selectedItemText: String) {
        val myDialog = CustomDialog()
        val bundle = Bundle()

        bundle.putString(TITLE_KEY, selectedItemText)
        myDialog.arguments = bundle

        myDialog.show(supportFragmentManager, null)
    }

    private fun saveDatatoDatabase() {
        database = ExerciseEntryDatabase.getInstance(this)
        databaseDao = database.exerciseEntryDatabaseDao
        repository = ExerciseEntryRepository(databaseDao)
        exerciseFactory = ExerciseEntryViewModelFactory(repository)
        exerciseEntryViewModel =
            ViewModelProvider(this, exerciseFactory).get(ExerciseEntryViewModel::class.java)

        val sharedPref: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext())
        val units = sharedPref.getString("units", "kilometers")
        val unitViewModel = ViewModelProvider(this)[UnitViewModel::class.java]
        unitViewModel.UNITS = units.toString()

        val exerciseEntryObj = ExerciseEntry()
        exerciseEntryObj.inputType = INPUT_TYPE_POSITION
        exerciseEntryObj.activityType = ACTIVITY_TYPE
        exerciseEntryObj.dateTime = "$TIME_SELECTED $DATE_SELECTED"
        exerciseEntryObj.duration = DURATION_ENTERED.toFloat()
        exerciseEntryObj.distance = convertMilesToKM(DISTANCE_ENTERED.toFloat(),units.toString())
        exerciseEntryObj.calorie = CALORIES_ENTERED.toFloat()
        exerciseEntryObj.heartrate = HEARTRATE_ENTERED.toInt()
        exerciseEntryObj.comment = COMMENT_ENTERED
        exerciseEntryViewModel.insert(exerciseEntryObj)
    }

    fun validateInputs(): Boolean{
        var missing_fields: MutableList<String> = mutableListOf<String>()
        if(DATE_SELECTED.isBlank()){
            missing_fields.add("Date")
        }
        else if(TIME_SELECTED.isBlank()){
            missing_fields.add("Time")
        }
        else if(DURATION_ENTERED.isBlank()){
            missing_fields.add("Duration")
        }
        else if(DISTANCE_ENTERED.isBlank()){
            missing_fields.add("Distance")
        }
        else if(CALORIES_ENTERED.isBlank()){
            missing_fields.add("Calories")
        }
        else if(HEARTRATE_ENTERED.isBlank()){
            missing_fields.add("Heartrate")
        }
        else if(HEARTRATE_ENTERED.isBlank()){
            missing_fields.add("comments")
        }

        if(missing_fields.size > 2){
            Toast.makeText(this, "Please enter all the inputs", Toast.LENGTH_SHORT).show()
            return false
        }
        if(missing_fields.size >= 1){
            var missing_items =""
            for (field in missing_fields){
                missing_items += "${field} "
            }
            Toast.makeText(this, "Please enter ${missing_items}", Toast.LENGTH_SHORT).show()
            return false
        }
        if (missing_fields.size == 0){
            return true
        }
        return false
    }
    fun OnButtonSave(view: View) {
        if (validateInputs()){
            saveDatatoDatabase()
            Toast.makeText(this, "Entry Saved!", Toast.LENGTH_SHORT).show()
            finish()
        }

    }

    fun OnButtonCancel(view: View) {
        Toast.makeText(this, "Entry Discarded", Toast.LENGTH_SHORT).show()
        finish()
    }

}


