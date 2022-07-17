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
import com.google.android.gms.maps.GoogleMap
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

    override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
        println("raman debug: onServiceConnected")
        val tempBinder = binder as NotifyService.MyBinder
        tempBinder.setgpsMessageHandler(myLocationHandler)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        println("raman debug: onServiceDisConnected")
    }

    inner class MyLocationHandler(looper: Looper): Handler(looper){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val bundle = msg.data
            val latitide = bundle.get(LATITUDE_LOCATION_KEY)
            val longitude = bundle.get(LONGITUDE_LOCATION_KEY)
            currentSpeed = bundle.getString(CURRENT_SPEED_KEY).toString()
            currentAltitude = bundle.getDouble(CURRENT_ALTITUDE_KEY)
            speedList.add(parseFloat(bundle.getString(CURRENT_SPEED_KEY).toString()))
            _location.value = LatLng(latitide as Double, longitude as Double)
        }
    }

}