package com.example.ramanpreet_sehmbi.ViewModels

import android.content.ComponentName
import android.content.ServiceConnection
import android.location.Location
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ramanpreet_sehmbi.Services.NotifyService
import com.example.ramanpreet_sehmbi.Services.NotifyService.Companion.CURRENT_ALTITUDE_KEY
import com.example.ramanpreet_sehmbi.Services.NotifyService.Companion.CURRENT_SPEED_KEY
import com.example.ramanpreet_sehmbi.Services.NotifyService.Companion.LATITUDE_LOCATION_KEY
import com.example.ramanpreet_sehmbi.Services.NotifyService.Companion.LONGITUDE_LOCATION_KEY
import com.example.ramanpreet_sehmbi.Services.NotifyService.Companion.PREDICTION_KEY
import com.example.ramanpreet_sehmbi.Services.NotifyService.Companion.TIME_ELAPSED
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import java.lang.Float.parseFloat

class GPSViewModel: ViewModel(), ServiceConnection {
    private val _location = MutableLiveData<LatLng>()
    val location: LiveData<LatLng>
    get() {
        return _location
    }

    private var myLocationHandler = MyLocationHandler(Looper.getMainLooper())
    var  markerOptions: MarkerOptions = MarkerOptions()
    var polylineOptions: PolylineOptions = PolylineOptions()
    var markerFinal: Marker? = null
    var isCenter = false
    var startingLocation: LatLng? = null


    var currentSpeed = ""
    var currentAltitude:Double = 0.0;
    var speedList = arrayListOf<Float>()
    var prediction = ""

    var oldLocation:Location? = null
    var currLocation:Location? = null

    var distanceBetweenPoints = 0f
    var final_distance = 0f
    var time_elapsed = 0

    fun distanceCalculator(latitide: Double, longitude:Double): Float {
        val currentLoc = Location("")
        currentLoc.latitude = latitide
        currentLoc.longitude = longitude
        currLocation = currentLoc

        if (oldLocation == null){
            oldLocation = currLocation
        }
        distanceBetweenPoints = (distanceBetweenPoints + (oldLocation?.distanceTo(currLocation)!!))
        oldLocation = currLocation
        return distanceBetweenPoints
    }
    override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
        val tempBinder = binder as NotifyService.MyBinder
        tempBinder.setgpsMessageHandler(myLocationHandler)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        println("raman debug: onServiceDisConnected")
    }
    private fun predictionCalculator(prediction_value: Int): String{
        if(prediction_value == 0){
            return "Standing"
        }
        else if (prediction_value == 1){
            return "Walking"
        }
        else if (prediction_value == 2){
            return "Running"
        }
        return "Unknown"
    }
    inner class MyLocationHandler(looper: Looper): Handler(looper){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val bundle = msg.data
            val latitide = bundle.get(LATITUDE_LOCATION_KEY)
            val longitude = bundle.get(LONGITUDE_LOCATION_KEY)
            currentSpeed = bundle.getString(CURRENT_SPEED_KEY, "").toString()
            currentAltitude = bundle.getInt(CURRENT_ALTITUDE_KEY, 0).toDouble()
            time_elapsed = bundle.getInt(TIME_ELAPSED,0)

            val speed = bundle.getString(CURRENT_SPEED_KEY)
            if (speed != null){
                speedList.add(parseFloat(speed))
            }
            if (latitide != null && longitude != null){
                _location.value = LatLng(latitide as Double, longitude as Double)
                final_distance = distanceCalculator(latitide, longitude)
            }

            val prediction_val = bundle.getInt(PREDICTION_KEY, -1)
            if (prediction_val != -1){
                prediction = predictionCalculator(prediction_val)
            }
        }
    }
}