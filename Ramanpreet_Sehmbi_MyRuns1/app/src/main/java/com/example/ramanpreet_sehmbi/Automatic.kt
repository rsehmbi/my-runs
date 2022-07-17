package com.example.ramanpreet_sehmbi

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.ramanpreet_sehmbi.Database.*
import com.example.ramanpreet_sehmbi.Services.NotifyService
import com.example.ramanpreet_sehmbi.UIHelpers.convertMilesToKM
import com.example.ramanpreet_sehmbi.ViewModels.GPSViewModel
import com.example.ramanpreet_sehmbi.databinding.ActivityAutomaticBinding
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import java.util.ArrayList

class Automatic : AppCompatActivity(), OnMapReadyCallback {

    private var INPUT_TYPE = "";
    private var ACTIVITY_TYPE= "";
    private var INPUT_TYPE_POSITION = -1

    private lateinit var database: ExerciseEntryDatabase
    private lateinit var databaseDao: ExerciseEntryDatabaseDao
    private lateinit var repository: ExerciseEntryRepository
    private lateinit var exerciseEntryViewModel: ExerciseEntryViewModel
    private lateinit var exerciseFactory: ExerciseEntryViewModelFactory

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityAutomaticBinding

    private lateinit var gpsViewModel: GPSViewModel;

    private var isBind = false
    private val BIND_STATUS_KEY = "BIND_STATUS_KEY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAutomaticBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        if (extras != null) {
            INPUT_TYPE = extras.getString("INPUT_TYPE").toString()
            INPUT_TYPE_POSITION = extras.getInt("INPUT_TYPE_POSITION")
            ACTIVITY_TYPE = extras.getString("ACTIVITY_TYPE").toString()
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        gpsViewModel = ViewModelProvider(this).get(GPSViewModel::class.java)

        if(savedInstanceState != null){
            println("The value of isBind is " + savedInstanceState.getBoolean(BIND_STATUS_KEY))
            isBind = savedInstanceState.getBoolean(BIND_STATUS_KEY)
        }
    }

     private fun addStartingMarker(currentLocation: LatLng){
        // This sets the first location update on the map. Once the location is set, you can return
        // because the next update should be done when the location changes.
         val nullLocation = LatLng(0.000000, 0.000000)
         mMap.addMarker(MarkerOptions().position(nullLocation).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)))

         val cameraUpdate: CameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLocation, 17f)
         mMap.animateCamera(cameraUpdate)
         gpsViewModel.markerOptions.position(currentLocation)
         mMap.addMarker(gpsViewModel.markerOptions)
         gpsViewModel.polylineOptions.add(currentLocation)
         mMap.addPolyline(gpsViewModel.polylineOptions)
         gpsViewModel.isCenter = true
    }

     private fun updateUI(lat: Double, lng: Double){
         val currentLocation = LatLng(lat, lng)
        if (!gpsViewModel.isCenter){
            addStartingMarker(currentLocation)
            gpsViewModel.startingLocation = currentLocation
            return
        }

         gpsViewModel.markerOptions.position(gpsViewModel.startingLocation)
         mMap.addMarker(gpsViewModel.markerOptions)

        if (gpsViewModel.markerFinal == null) {
            // This means adding final marker for first time
            gpsViewModel.markerFinal = mMap.addMarker(MarkerOptions().position(currentLocation))
            gpsViewModel.polylineOptions.add(currentLocation)
            gpsViewModel.markerFinal!!.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            mMap.addPolyline( gpsViewModel.polylineOptions)
        } else {
            gpsViewModel.markerFinal!!.remove()
            gpsViewModel.markerFinal = mMap.addMarker(MarkerOptions().position(currentLocation))
            gpsViewModel.markerFinal!!.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            gpsViewModel.markerFinal!!.setPosition(currentLocation)
            gpsViewModel.polylineOptions.add(currentLocation)
            mMap.addPolyline(gpsViewModel.polylineOptions)
            val cameraUpdate: CameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLocation, 17f)
            mMap.animateCamera(cameraUpdate)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        gpsViewModel.polylineOptions.color(Color.BLACK)
        // Blue is the final marker
        gpsViewModel.location.observe(this){
            updateUI(it.latitude, it.longitude)
        }
        checkPermission()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) startNotifyService(); bindNotifyService()
        }
    }

    override fun onBackPressed(){
        stopService()
        super.onBackPressed()
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
    fun savetoDatabase(){
        database = ExerciseEntryDatabase.getInstance(this)
        databaseDao = database.exerciseEntryDatabaseDao
        repository = ExerciseEntryRepository(databaseDao)
        exerciseFactory = ExerciseEntryViewModelFactory(repository)
        exerciseEntryViewModel =
            ViewModelProvider(this, exerciseFactory).get(ExerciseEntryViewModel::class.java)

        val exerciseEntryObj = ExerciseEntry()
        exerciseEntryObj.inputType = INPUT_TYPE_POSITION
        exerciseEntryObj.activityType = ACTIVITY_TYPE
        exerciseEntryObj.dateTime = "datetimetest"
        exerciseEntryObj.duration = 13f
        exerciseEntryObj.distance = 14f
        exerciseEntryObj.calorie = 15f
        exerciseEntryObj.heartrate = 126
        exerciseEntryObj.comment = "Testret"
        exerciseEntryObj.locationList = gpsViewModel.polylineOptions.points as ArrayList<LatLng>

        exerciseEntryViewModel.insert(exerciseEntryObj)

    }
    fun OnButtonSave(view: View) {
        savetoDatabase()
        println("raman debug: The points are" + gpsViewModel.polylineOptions.points)
        println("raman debug: The isCenter is" + gpsViewModel.isCenter)
        Toast.makeText(this, "Entry Saved", Toast.LENGTH_SHORT).show()
    }

    fun OnButtonCancel(view: View) {
        stopService()
        Toast.makeText(this, "Entry Discarded", Toast.LENGTH_SHORT).show()
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

    private fun stopService(){
        unBindService()
        val serviceIntent = Intent(this, NotifyService::class.java)
        this.stopService(serviceIntent)
    }

    private fun unBindService(){
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