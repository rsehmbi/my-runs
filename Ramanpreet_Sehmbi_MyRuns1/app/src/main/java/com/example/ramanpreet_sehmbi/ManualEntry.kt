package com.example.ramanpreet_sehmbi

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.ramanpreet_sehmbi.CustomDialog.Companion.TITLE_KEY
import com.example.ramanpreet_sehmbi.Database.*
import java.util.*


class ManualEntry : AppCompatActivity() {

    lateinit var manualEntries: ListView
    private val calendar = Calendar.getInstance()

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
        println("Input Type is " + INPUT_TYPE)
        println("Activity Type is " + INPUT_TYPE)
    }

    fun setUpManualEntriesList(){
        manualEntries = findViewById(R.id.manual_list_id)
        val manualEntriesAdapter = ArrayAdapter.createFromResource(this, R.array.manual_list_items, android.R.layout.simple_list_item_1);
        manualEntries.adapter = manualEntriesAdapter
        manualEntries.onItemClickListener = AdapterView.OnItemClickListener {
                parent, view, position, id ->
            val selectedItemText = parent.getItemAtPosition(position).toString()
            if (selectedItemText == "Date"){
                handleDateClicked()
            }
            else if (selectedItemText == "Time"){
                handleTimeClicked()
            }
            else{
                showPopUpDialog(selectedItemText)
                handleDialogResult()
            }
        }
    }

    fun handleDialogResult(){
        supportFragmentManager.setFragmentResultListener("CUSTOM_DIALOG_REQUEST_KEY", this){
                resultkey, bundle ->
            if (resultkey == "CUSTOM_DIALOG_REQUEST_KEY") {
                if (bundle.containsKey("DISTANCE_ENTERED")){
                    DISTANCE_ENTERED = bundle.get("DISTANCE_ENTERED").toString()
                }
                else if (bundle.containsKey("DURATION_ENTERED")){
                    DURATION_ENTERED = bundle.get("DURATION_ENTERED").toString()
                }
                else if (bundle.containsKey("CALORIES_ENTERED")){
                    CALORIES_ENTERED = bundle.get("CALORIES_ENTERED").toString()
                }
                else if (bundle.containsKey("HEARTRATE_ENTERED")){
                    HEARTRATE_ENTERED = bundle.get("HEARTRATE_ENTERED").toString()
                }
                else if (bundle.containsKey("COMMENT_ENTERED")){
                    COMMENT_ENTERED = bundle.get("COMMENT_ENTERED").toString()
                }
            }
        }
    }

    fun handleDateClicked(){
        val datePickerFragment = com.example.ramanpreet_sehmbi.DatePicker()
        datePickerFragment.show(supportFragmentManager, null)
        supportFragmentManager.setFragmentResultListener("DATE_REQUEST_KEY", this){
            resultkey, bundle ->
            if (resultkey == "DATE_REQUEST_KEY"){
                DATE_SELECTED = bundle.get("DATE_SELECTED").toString()
                Toast.makeText(this, "${DATE_SELECTED}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun handleTimeClicked(){
        val timePickerFragment = com.example.ramanpreet_sehmbi.TimePicker()
        timePickerFragment.show(supportFragmentManager, null)
        supportFragmentManager.setFragmentResultListener("TIME_REQUEST_KEY", this){
                resultkey, bundle ->
            if (resultkey == "TIME_REQUEST_KEY"){
                TIME_SELECTED = bundle.get("TIME_SELECTED").toString()
                Toast.makeText(this, "${TIME_SELECTED}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun showPopUpDialog(selectedItemText:String){
        val myDialog = CustomDialog()
        val bundle = Bundle()

        bundle.putString(TITLE_KEY, selectedItemText)
        myDialog.arguments = bundle

        myDialog.show(supportFragmentManager, null)
    }

    private fun saveDatatoDatabase(){
        database = ExerciseEntryDatabase.getInstance(this)
        databaseDao = database.exerciseEntryDatabaseDao
        repository = ExerciseEntryRepository(databaseDao)
        exerciseFactory = ExerciseEntryViewModelFactory(repository)
        exerciseEntryViewModel = ViewModelProvider(this, exerciseFactory).get(ExerciseEntryViewModel::class.java)
        val exerciseEntryObj = ExerciseEntry()

        exerciseEntryObj.inputType = INPUT_TYPE_POSITION
        exerciseEntryObj.activityType = ACTIVITY_TYPE
        exerciseEntryObj.dateTime = DATE_SELECTED + TIME_SELECTED
        exerciseEntryObj.duration = DURATION_ENTERED.toInt()
        exerciseEntryObj.distance = DISTANCE_ENTERED.toFloat()
        exerciseEntryObj.calorie = CALORIES_ENTERED.toFloat()
        exerciseEntryObj.heartrate = HEARTRATE_ENTERED.toInt()
        exerciseEntryObj.comment = COMMENT_ENTERED
        exerciseEntryViewModel.insert(exerciseEntryObj)

    }
    fun OnButtonSave(view: View) {
        saveDatatoDatabase()
        finish()
    }
    fun OnButtonCancel(view: View) {
        Toast.makeText(this, "Entry Discarded", Toast.LENGTH_SHORT).show()
        finish()
    }

}


