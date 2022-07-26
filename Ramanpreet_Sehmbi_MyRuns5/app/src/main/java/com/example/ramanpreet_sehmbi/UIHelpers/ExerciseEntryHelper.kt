package com.example.ramanpreet_sehmbi.UIHelpers

import com.example.ramanpreet_sehmbi.MONTHS_YEAR
import java.lang.Float.parseFloat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

fun convertTypeIntToString(position: String): String {
    // Helps in showing ui string as shown in demo app
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
    // Helps in showing ui string as shown in demo app
    return "$heartRate bpm"
}

fun getCalorieString(cals: String): String {
    // Helps in showing ui string as shown in demo app
    return "$cals cals"
}

fun convertMetrics(kilometers: String, metric_system: String): String {
    // Helps in showing ui string as shown in demo app
    if (metric_system.contains("imperial")) {
        var miles = (kilometers.toFloat() / 1.609f)
        return "$miles Miles"
    }
    return "$kilometers Kilometers"
}

fun convertMilesToKM(distance: Float, metric_system: String): Float {
    // Convert Miles to Kilometers
    if (metric_system.contains("imperial")) {
        return distance * 1.609f
    }
    return distance
}

fun getCurrentDateTime(): String{
    val calendar: Calendar = Calendar.getInstance(TimeZone.getDefault())
    val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date());
    val year = calendar.get(Calendar.YEAR)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val month = MONTHS_YEAR[calendar.get(Calendar.MONTH)]
    return "${time} ${month} ${day} ${year}"
}

fun covertIntoKm(speed: String, units:String): String {
    if(speed != "" && speed != null){
        if (units.contains("imperial"))
            return "Curr Speed: " + String.format("%.2f", (2.23694 * parseFloat(speed))) + " m/h"
        else
            return "Curr Speed: " + String.format("%.2f", (3.6 * parseFloat(speed))) + " km/h"
    }
    return "Curr Speed: "
}

fun convertAltitude(altitude: Double, units:String): String {
    if (units.contains("imperial"))
        return "Climb: " + String.format("%.2f", (0.000621371 * altitude)) + " miles"
    else
        return "Climb: " + String.format("%.2f", (0.001 * altitude) )+ " kms"
}

fun covertAvgSpeed(speed: Double, units:String): String {
    if (units.contains("imperial"))
        return "Avg Speed: " + String.format("%.2f", (2.23694 * speed)) + " m/h"
    else
        return "Avg Speed: " + String.format("%.2f", (3.6 * speed)) + " km/h"
}

fun covertDistance(distance: Float, units:String): String {
    if (units.contains("imperial"))
        return "Distance: " + String.format("%.2f", (0.000621371 * distance)) + " miles"
    else
        return "Distance: " + String.format("%.2f", (0.001 * distance)) + " kms"
}

fun covertCalories(distance: Float): String {
    return "Calories: " + String.format("%.2f", (distance * 0.001) * 62)
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

