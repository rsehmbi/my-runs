package com.example.ramanpreet_sehmbi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class GPS : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gps)
    }

    fun OnButtonSave(view: View) {
        finish()
    }

    fun OnButtonCancel(view: View) {
        finish()
    }
}