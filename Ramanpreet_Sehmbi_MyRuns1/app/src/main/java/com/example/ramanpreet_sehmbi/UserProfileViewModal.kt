package com.example.ramanpreet_sehmbi

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserProfileViewModal: ViewModel() {
    // Reference: Prof's class example
    val userProfileImage = MutableLiveData<Bitmap>()
}