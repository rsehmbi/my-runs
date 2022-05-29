package com.example.ramanpreet_sehmbi

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import java.util.*

class ManualEntry : AppCompatActivity(), TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

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
        val datePicker = DatePickerDialog(this,  R.style.DialogTheme,this,  calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH))
        datePicker.show()
    }

    fun handleTimeClicked(){
        val timePicker = TimePickerDialog(this, R.style.DialogTheme, this,calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
        timePicker.show()
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

    override fun onTimeSet(view: TimePicker, hour: Int, minute: Int) {
        //TODO: Requirements not specified yet
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        //TODO: Requirements not specified yet
    }
}


