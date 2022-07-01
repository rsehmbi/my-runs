package com.example.ramanpreet_sehmbi

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.ramanpreet_sehmbi.ViewModels.UnitViewModel

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        val key = preference.key.toString()
        if(key == "user_profile"){
            val userProfileIntent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(userProfileIntent)
        }
        else if (key == "webpage"){
            val webpageIntent = Intent(Intent.ACTION_VIEW)
            webpageIntent.data = Uri.parse("https://www.sfu.ca/computing.html")
            startActivity(webpageIntent)
        }
        return super.onPreferenceTreeClick(preference)
    }

    override fun onSharedPreferenceChanged(preference: SharedPreferences?, p1: String?) {
        val key = preference?.getString("units","")
        val unitViewModel = ViewModelProvider(requireActivity())[UnitViewModel::class.java]
        unitViewModel.UNITS = key.toString()
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences
            ?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences
            ?.unregisterOnSharedPreferenceChangeListener(this)
    }
}