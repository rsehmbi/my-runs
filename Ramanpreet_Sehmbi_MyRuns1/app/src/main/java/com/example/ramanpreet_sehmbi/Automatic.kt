package com.example.ramanpreet_sehmbi

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.*
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.ramanpreet_sehmbi.Services.NotifyService
import com.example.ramanpreet_sehmbi.ViewModels.GPSViewModel
import com.example.ramanpreet_sehmbi.databinding.ActivityAutomaticBinding
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import java.util.*


class Automatic : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityAutomaticBinding
    private lateinit var  markerOptions: MarkerOptions
    private var isCenter = false
    private lateinit var polylineOptions: PolylineOptions

    private lateinit var gpsViewModel: GPSViewModel;
    private var markerFinal: Marker? = null
    var iterator = 0;
    private val BIND_STATUS_KEY = "BIND_STATUS_KEY"
    var isBind = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAutomaticBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        gpsViewModel = ViewModelProvider(this).get(GPSViewModel::class.java)
        gpsViewModel.location.observe(this){
            updateUI(it.latitude, it.longitude)
        }

        if(savedInstanceState != null){
            isBind = savedInstanceState.getBoolean(BIND_STATUS_KEY)
        }
    }

     fun addStartingMarker(currentLocation: LatLng){
        // This sets the first location update on the map. Once the location is set, you can return
        // because the next update should be done when the location changes.
        val cameraUpdate: CameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLocation, 17f)
        mMap.animateCamera(cameraUpdate)
        markerOptions.position(currentLocation)
        mMap.addMarker(markerOptions)
        polylineOptions.add(currentLocation)
        mMap.addPolyline(polylineOptions)
        isCenter = true

    }

     fun updateUI(lat: Double, lng: Double){
         val currentLocation = LatLng(lat, lng)

        val nullLocation = LatLng(0.000000, 0.000000)
        mMap.addMarker(MarkerOptions().position(nullLocation).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))

        if (!isCenter){
            addStartingMarker(currentLocation)
            return
        }

        if (markerFinal == null) {
            // This means adding final marker for first time
            markerFinal = mMap.addMarker(MarkerOptions().position(currentLocation))
            polylineOptions.add(currentLocation)
            markerFinal!!.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            mMap.addPolyline(polylineOptions)
        } else {
            markerFinal!!.setPosition(currentLocation)
            polylineOptions.add(currentLocation)
            mMap.addPolyline(polylineOptions)
            val cameraUpdate: CameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLocation, 17f)
            mMap.animateCamera(cameraUpdate)
        }
    }

    fun printLocationonConsole(lat: Double, lng: Double){
        var line2 = ""
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(lat, lng, 1)
        val address = addresses.get(0)

        for (i in 0..address.maxAddressLineIndex)
            line2 += "${address.getAddressLine(i)}\n"

        println("The last location is "+ line2 )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        markerOptions = MarkerOptions()
        polylineOptions = PolylineOptions()
        polylineOptions.color(Color.BLACK)
        checkPermission()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) startNotifyService(); bindNotifyService()
        }
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
            startNotifyService()
            bindNotifyService()
        }
    }

//    fun initLocationManager() {
//        try{
//            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
//            val criteria = Criteria()
//            criteria.accuracy = Criteria.ACCURACY_FINE
//            val provider: String? = locationManager.getBestProvider(criteria, true)
//            if (provider!=null){
//                val location = locationManager.getLastKnownLocation(provider)
//                if(location != null){
//                    onLocationChanged(location)
//                }
//                locationManager.requestLocationUpdates(provider, 0, 0.1f, this)
//            }
//
//        } catch (e: SecurityException){
//            println("ERROR")
//        }
//
//    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun OnButtonSave(view: View) {
        startNotifyService()
        Toast.makeText(this, "Start", Toast.LENGTH_SHORT).show()
    }
    fun OnButtonCancel(view: View) {
        stopService()
        Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show()
    }
    fun OnBindSave(view: View) {
        bindNotifyService()
        Toast.makeText(this, "Bind", Toast.LENGTH_SHORT).show()
    }
    fun OnUnBindCancel(view: View) {
        unBindService()
        Toast.makeText(this, "UnBind", Toast.LENGTH_SHORT).show()
    }

    private fun startNotifyService(){
        val serviceIntent = Intent(this, NotifyService::class.java)
        this.applicationContext.startService(serviceIntent)
    }

    private fun bindNotifyService(){
        if(!isBind){
            val serviceIntent = Intent(this, NotifyService::class.java)
            this.applicationContext.bindService(serviceIntent,gpsViewModel ,Context.BIND_AUTO_CREATE)
            isBind = true
        }
    }

    fun stopService(){
        unBindService()
        val serviceIntent = Intent(this, NotifyService::class.java)
        this.applicationContext.stopService(serviceIntent)
    }
    fun unBindService(){
        if(isBind){
            this.applicationContext.unbindService(gpsViewModel)
            isBind = false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(BIND_STATUS_KEY, isBind)
    }
}