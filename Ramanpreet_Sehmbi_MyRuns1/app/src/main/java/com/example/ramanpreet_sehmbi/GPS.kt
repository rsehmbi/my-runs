package com.example.ramanpreet_sehmbi

import android.Manifest
import android.content.pm.PackageManager
import android.location.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

class GPS : AppCompatActivity(), LocationListener {

    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gps)

        checkPermission()
    }

    private fun checkPermission() {
        if(Build.VERSION.SDK_INT < 23) {
            return
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),0)
        }
        else{
            initLocationManager()
        }
    }

    override fun onLocationChanged(location: Location){
        val lat = location.latitude
        val lon = location.longitude

        var line2 = ""
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(lat, lon, 1)
        val address = addresses.get(0)

        for (i in 0..address.maxAddressLineIndex)
            line2 += "${address.getAddressLine(i)}\n"

        println("The last location is "+ line2 )
    }

     fun initLocationManager() {
        try{
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_FINE
            val provider: String? = locationManager.getBestProvider(criteria, true)
            if (provider!=null){
                val location = locationManager.getLastKnownLocation(provider)
                if(location != null){
                    onLocationChanged(location)
                }
                locationManager.requestLocationUpdates(provider, 0, 1f, this)
            }

        } catch (e: SecurityException){
            println("ERROR")
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) initLocationManager()
        }
    }

    fun OnButtonSave(view: View) {
        finish()
    }

    fun OnButtonCancel(view: View) {
        finish()
    }
}