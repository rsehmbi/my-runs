package com.example.ramanpreet_sehmbi

import android.os.Bundle
import android.view.View
import android.widget.*

import androidx.appcompat.app.AppCompatActivity

import java.util.*


class ManualEntry : AppCompatActivity() {

    lateinit var manualEntries: ListView
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_entry)
        setUpManualEntriesList()

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
                showPopUpDialod(selectedItemText)
            }
        }
    }
    fun handleDateClicked(){
        val datePickerFragment = com.example.ramanpreet_sehmbi.DatePicker()
        datePickerFragment.show(supportFragmentManager, null)
        supportFragmentManager.setFragmentResultListener("DATE_REQUEST_KEY", this){
            resultkey, bundle ->
            if (resultkey == "DATE_REQUEST_KEY"){
                val date = bundle.get("DATE_SELECTED")
                Toast.makeText(this, "$date", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun handleTimeClicked(){
        val timePickerFragment = com.example.ramanpreet_sehmbi.TimePicker()
        timePickerFragment.show(supportFragmentManager, null)
        supportFragmentManager.setFragmentResultListener("TIME_REQUEST_KEY", this){
                resultkey, bundle ->
            if (resultkey == "TIME_REQUEST_KEY"){
                val time = bundle.get("TIME_SELECTED")
                Toast.makeText(this, "$time", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun showPopUpDialod(selectedItemText:String){
        //TODO: Requirements not available yet

    }
    fun OnButtonSave(view: View) {
        finish()
    }
    fun OnButtonCancel(view: View) {
        finish()
    }

}


