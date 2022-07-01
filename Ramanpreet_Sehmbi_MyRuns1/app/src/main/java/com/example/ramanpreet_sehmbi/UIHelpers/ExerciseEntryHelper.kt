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

fun convertMetrics(kilometers: String, metric_system:String): String {
    if (metric_system.contains("imperial")){
        var miles = kilometers.toFloat()
        miles *= 0.621371f
        return "$miles Miles"
    }
    return "$kilometers Kilometers"
}
