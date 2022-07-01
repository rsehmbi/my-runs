package com.example.ramanpreet_sehmbi.UIHelpers

import android.app.Activity
import android.content.SharedPreferences
import android.widget.Toast
import androidx.preference.PreferenceManager


fun convertTypeIntToString(position: String): String {
    val position_int = position.toInt()
    val entrytype = arrayOf<String>(
        "Manual Entry",
        "GPS",
        "Automatic",
    )
    if (position_int >= 0 && position_int < 3){
        return entrytype[position_int]
    }
    return position
}

fun kilometerConversion(activity: Activity){
    val sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext())
    val units = sharedPref.getString("units", "")
    Toast.makeText(activity, "${units}", Toast.LENGTH_SHORT).show()
}
