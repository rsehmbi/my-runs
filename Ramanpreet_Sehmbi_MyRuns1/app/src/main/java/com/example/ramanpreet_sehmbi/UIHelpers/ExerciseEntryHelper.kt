package com.example.ramanpreet_sehmbi.UIHelpers

public fun convertTypeIntToString(position: String): String {
    val position_int = position.toInt()
    println("The positions are "+ position_int)
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