package com.example.ramanpreet_sehmbi

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        val key = preference!!.key.toString()
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
}