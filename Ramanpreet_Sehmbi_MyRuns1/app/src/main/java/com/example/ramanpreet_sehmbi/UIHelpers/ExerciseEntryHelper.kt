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

