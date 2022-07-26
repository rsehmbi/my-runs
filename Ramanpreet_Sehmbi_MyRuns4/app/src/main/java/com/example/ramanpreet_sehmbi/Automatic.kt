package com.example.ramanpreet_sehmbi

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.ramanpreet_sehmbi.Database.*
import com.example.ramanpreet_sehmbi.Services.NotifyService
import com.example.ramanpreet_sehmbi.UIHelpers.*
import com.example.ramanpreet_sehmbi.ViewModels.GPSViewModel
import com.example.ramanpreet_sehmbi.databinding.ActivityAutomaticBinding
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import java.util.*
import kotlin.collections.ArrayList

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

    lateinit var typeTextView: TextView;
    lateinit var avgSpeedTextView: TextView;
    lateinit var currSpeedTextView: TextView;
    lateinit var climbTextView: TextView;
    lateinit var calorieTextView: TextView;
    lateinit var distanceTextView: TextView;

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

        typeTextView = findViewById(R.id.activity_type_id)
        if (ACTIVITY_TYPE == "AUTOMATIC"){
            typeTextView.text = "Type: Unknown"
        }
        else{
            typeTextView.text = "Type: "+ ACTIVITY_TYPE
        }
        avgSpeedTextView = findViewById(R.id.avg_speed_id)
        currSpeedTextView = findViewById(R.id.curr_speed_id)
        climbTextView = findViewById(R.id.climb_id)
        calorieTextView = findViewById(R.id.calorie_id)
        distanceTextView = findViewById(R.id.distance_id)
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
         updateTextData()
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

    private fun updateTextData(){
        val sharedPref: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext())
        val units = sharedPref.getString("units", "")

        if (ACTIVITY_TYPE == "AUTOMATIC"){
            typeTextView.text = "Type: "+ gpsViewModel.prediction
        }
        currSpeedTextView.text = covertIntoKm(gpsViewModel.currentSpeed, units!!)
        climbTextView.text = convertAltitude(gpsViewModel.currentAltitude, units!!)
        avgSpeedTextView.text =covertAvgSpeed(gpsViewModel.speedList.average(), units!!)
        distanceTextView.text =covertDistance(gpsViewModel.final_distance, units!!)
        calorieTextView.text = covertCalories(gpsViewModel.final_distance)
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

        if (ACTIVITY_TYPE == "AUTOMATIC"){
            exerciseEntryObj.activityType = gpsViewModel.prediction
        }
        else {
            exerciseEntryObj.activityType = ACTIVITY_TYPE
        }
        exerciseEntryObj.dateTime = getCurrentDateTime()
        exerciseEntryObj.duration = (gpsViewModel.time_elapsed.toFloat() / 60)
        exerciseEntryObj.distance = gpsViewModel.final_distance
        exerciseEntryObj.calorie = gpsViewModel.final_distance
        exerciseEntryObj.avgSpeed = gpsViewModel.speedList.average().toFloat()
        if (gpsViewModel.currentSpeed != ""){
            exerciseEntryObj.avgPace = gpsViewModel.currentSpeed.toFloat()
        }
        else {
            exerciseEntryObj.avgPace = 0.0f
        }
        exerciseEntryObj.climb = gpsViewModel.currentAltitude.toFloat()
        exerciseEntryObj.locationList = gpsViewModel.polylineOptions.points as ArrayList<LatLng>

        exerciseEntryViewModel.insert(exerciseEntryObj)

    }

    fun OnButtonSave(view: View) {
        savetoDatabase()
        stopService()
        Toast.makeText(this, "Entry Saved", Toast.LENGTH_SHORT).show()
        finish()
    }

    fun OnButtonCancel(view: View) {
        stopService()
        Toast.makeText(this, "Entry Discarded", Toast.LENGTH_SHORT).show()
        finish()
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