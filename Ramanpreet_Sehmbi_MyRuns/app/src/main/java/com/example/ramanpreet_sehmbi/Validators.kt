package com.example.ramanpreet_sehmbi

import android.util.Patterns


fun validatePhonenumber(phonenumber: CharSequence): Boolean {
    if (!phonenumber.contains("(")) {
        if (phonenumber.length > 10)
            return false
    }
    if (phonenumber.length in 4..13) {
        return false
    }
    return !phonenumber.isNullOrEmpty() && Patterns.PHONE.matcher(phonenumber).matches()
}

fun validateEmail(email: CharSequence): Boolean {
    return !email.isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
}