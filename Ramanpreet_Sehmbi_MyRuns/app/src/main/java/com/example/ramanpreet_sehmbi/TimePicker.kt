package com.example.ramanpreet_sehmbi

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import java.text.SimpleDateFormat
import java.util.*

class TimePicker : DialogFragment(), TimePickerDialog.OnTimeSetListener {
    private val calendar = Calendar.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return TimePickerDialog(
            requireActivity(),
            R.style.DialogTheme,
            this,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
    }

    override fun onTimeSet(p0: TimePicker?, hour: Int, minute: Int) {
        val selectedTime = "$hour:$minute"
        val selectedTimeBundle = Bundle()
        selectedTimeBundle.putString("TIME_SELECTED", selectedTime)
        setFragmentResult("TIME_REQUEST_KEY", selectedTimeBundle)
    }
}