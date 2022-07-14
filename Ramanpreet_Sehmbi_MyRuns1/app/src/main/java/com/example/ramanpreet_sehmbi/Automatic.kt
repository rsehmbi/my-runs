package com.example.ramanpreet_sehmbi

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.*
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ramanpreet_sehmbi.databinding.ActivityAutomaticBinding
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import java.util.*


class Automatic : AppCompatActivity(), OnMapReadyCallback, LocationListener, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityAutomaticBinding
    private lateinit var locationManager: LocationManager
    private lateinit var  markerOptions: MarkerOptions
    private var isCenter = false
    private lateinit var polylineOptions: PolylineOptions

    private var markerFinal: Marker? = null
    var iterator = 0;
    private lateinit var polylines: ArrayList<Polyline>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAutomaticBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

     fun addStartingMarker(currentLocation: LatLng){
        // This sets the first location update on the map. Once the location is set, you can return
        // because the next update should be done when the location chanages.
        val cameraUpdate: CameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLocation, 17f)
        mMap.animateCamera(cameraUpdate)
        markerOptions.position(currentLocation)
        mMap.addMarker(markerOptions)
        polylineOptions.add(currentLocation)
        mMap.addPolyline(polylineOptions)
        isCenter = true

    }

    override fun onLocationChanged(location: Location){
        val lat = location.latitude
        val lng = location.longitude
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
        printLocationonConsole(lat, lng)
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
        mMap.setOnMapClickListener(this)
        mMap.setOnMapLongClickListener(this)
        markerOptions = MarkerOptions()
        polylineOptions = PolylineOptions()
        polylineOptions.color(Color.BLACK)
        checkPermission()

    }


    override fun onMapClick(latLng: LatLng) {
        println("debug: I clicked on map")
    }

    override fun onMapLongClick(latLng: LatLng) {
        println("debug: I long clicked on map")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) initLocationManager()
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
            initLocationManager()
        }
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
                locationManager.requestLocationUpdates(provider, 0, 0.1f, this)
            }

        } catch (e: SecurityException){
            println("ERROR")
        }

    }
    override fun onDestroy() {
        super.onDestroy()
        if (locationManager != null){
            locationManager.removeUpdates(this)
        }
    }
}