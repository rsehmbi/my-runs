package com.example.ramanpreet_sehmbi.ViewModels

import android.app.Activity
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager

class UnitViewModel() : ViewModel() {
    // Unit view model to handle the unit changes
    var UNITS = "metric"
}