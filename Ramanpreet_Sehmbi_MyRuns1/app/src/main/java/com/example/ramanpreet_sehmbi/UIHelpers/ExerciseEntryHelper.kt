package com.example.ramanpreet_sehmbi.UIHelpers

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
        var miles = kilometers.toFloat()
        miles *= 0.621371f
        return "$miles Miles"
    }
    return "$kilometers Kilometers"
}

fun convertMilesToKM(miles: String, metric_system: String): String {


    return miles
}

