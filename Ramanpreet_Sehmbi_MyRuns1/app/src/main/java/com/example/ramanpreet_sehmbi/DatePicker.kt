package com.example.ramanpreet_sehmbi

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import java.text.SimpleDateFormat
import java.util.*

class DatePicker : DialogFragment(), DatePickerDialog.OnDateSetListener {
    private val calendar = Calendar.getInstance()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return DatePickerDialog(
            requireActivity(),
            R.style.DialogTheme,
            this,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        val selectedDate = "$day-$month-$year"
        val selectedDateBundle = Bundle()
        selectedDateBundle.putString("DATE_SELECTED", selectedDate)
        setFragmentResult("DATE_REQUEST_KEY", selectedDateBundle)
    }

}