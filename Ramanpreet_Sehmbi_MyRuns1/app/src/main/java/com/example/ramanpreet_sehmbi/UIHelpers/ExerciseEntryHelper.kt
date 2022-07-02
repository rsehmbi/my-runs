package com.example.ramanpreet_sehmbi.UIHelpers

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

fun convertTypeIntToString(position: String): String {
    val position_int = position.toInt()
    val entrytype = arrayOf<String>(
        "Manual Entry",
        "GPS",
        "Automatic",
    )
    if (position_int in 0..2) {
        return entrytype[position_int]
    }
    return position
}

fun getHeartRateString(heartRate: String): String {
    return "$heartRate bpm"
}

fun getCalorieString(cals: String): String {
    return "$cals cals"
}

fun convertMetrics(kilometers: String, metric_system: String): String {
    if (metric_system.contains("imperial")) {
        var miles = (kilometers.toFloat() / 1.609f)
        return "$miles Miles"
    }
    return "$kilometers Kilometers"
}

fun convertMilesToKM(distance: Float, metric_system: String): Float {
    if (metric_system.contains("imperial")) {
        return distance * 1.609f
    }
    return distance
}

fun convertIntToTime(time: Float):String
{
    // Convert input in minutes
    val timed = time * 60
    var strTemp = String()
    val minutes: Int = (timed / 60).toInt()
    val seconds: Int = (timed % 60).toInt()
    strTemp =
        if (minutes < 10) "0" + Integer.toString(minutes) + "mins " else Integer.toString(minutes) + "mins "
    strTemp =
        if (seconds < 10) strTemp + "0" + Integer.toString(seconds) + "secs" else strTemp + Integer.toString(seconds) + "secs"
    return strTemp
}

