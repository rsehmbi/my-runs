package com.example.ramanpreet_sehmbi.TypeConverter

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class LatLongConverter {
    @TypeConverter
    fun fromString(value: String): ArrayList<LatLng> {
        val listType: Type = object : TypeToken<ArrayList<LatLng>>() {}.getType()
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: ArrayList<LatLng>): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}