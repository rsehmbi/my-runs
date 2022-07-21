package com.example.ramanpreet_sehmbi

import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.ramanpreet_sehmbi.Database.*
import com.example.ramanpreet_sehmbi.UIHelpers.*

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.ramanpreet_sehmbi.databinding.ActivityGpsentryBinding
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.PolylineOptions

class gpsentry : AppCompatActivity(), OnMapReadyCallback {
    var entryId: String = ""
    private lateinit var mMap: GoogleMap
private lateinit var binding: ActivityGpsentryBinding

    private lateinit var database: ExerciseEntryDatabase
    private lateinit var databaseDao: ExerciseEntryDatabaseDao
    private lateinit var repository: ExerciseEntryRepository
    private lateinit var exerciseEntryViewModel: ExerciseEntryViewModel
    private lateinit var exerciseFactory: ExerciseEntryViewModelFactory

    lateinit var typeTextView: TextView;
    lateinit var avgSpeedTextView: TextView;
    lateinit var currSpeedTextView: TextView;
    lateinit var climbTextView: TextView;
    lateinit var calorieTextView: TextView;
    lateinit var distanceTextView: TextView;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

     binding = ActivityGpsentryBinding.inflate(layoutInflater)
     setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        typeTextView = findViewById(R.id.activity_type_id)
        avgSpeedTextView = findViewById(R.id.avg_speed_id)
        currSpeedTextView = findViewById(R.id.curr_speed_id)
        climbTextView = findViewById(R.id.climb_id)
        calorieTextView = findViewById(R.id.calorie_id)
        distanceTextView = findViewById(R.id.distance_id)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Add a marker in Sydney and move the camera
        val nullLocation = LatLng(00.0, 00.0)
        mMap.addMarker(MarkerOptions().position(nullLocation))

        database = ExerciseEntryDatabase.getInstance(this)
        databaseDao = database.exerciseEntryDatabaseDao
        repository = ExerciseEntryRepository(databaseDao)
        exerciseFactory = ExerciseEntryViewModelFactory(repository)
        exerciseEntryViewModel =
            ViewModelProvider(this, exerciseFactory).get(ExerciseEntryViewModel::class.java)


        exerciseEntryViewModel.allExerciseEntriesLiveData.observe(this) {
            val extras = intent.extras

            if (extras != null) {
                entryId = extras.getString("EXERCISE_ENTRY_ID").toString()
            }
            for (entry in it) {
                if (entry.id.toString() == entryId) {
                    val sharedPref: SharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext())
                    val units = sharedPref.getString("units", "")

                    typeTextView.text = "Type: " + entry.activityType
                    currSpeedTextView.text = covertIntoKm(entry.avgSpeed.toString(), units!!)
                    climbTextView.text = convertAltitude(entry.climb.toDouble(), units!!)
                    avgSpeedTextView.text = "Avg Speed: " + entry.avgPace.toString()
                    distanceTextView.text = covertDistance(entry.distance, units!!)
                    calorieTextView.text = "Calories: " + entry.calorie.toString()

                    val polylineOptions = PolylineOptions()
                    polylineOptions.color(Color.BLACK)

                    for (latlng in entry.locationList)
                        polylineOptions.add(latlng)

                    val startingLocation = LatLng(entry.locationList[0].latitude,entry.locationList[0].longitude )
                    val finalLocation = LatLng(entry.locationList[entry.locationList.size -1].latitude,entry.locationList[entry.locationList.size-1].longitude )
                    mMap.addPolyline(polylineOptions)

                    mMap.addMarker(MarkerOptions().
                    position(startingLocation).
                    icon(BitmapDescriptorFactory.
                    defaultMarker(BitmapDescriptorFactory.HUE_RED)))

                    mMap.addMarker(MarkerOptions().
                    position(finalLocation).
                    icon(BitmapDescriptorFactory.
                    defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))

                    val cameraUpdate: CameraUpdate = CameraUpdateFactory.newLatLngZoom(finalLocation, 15f)
                    mMap.animateCamera(cameraUpdate)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.delete_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_delete_id -> {
            exerciseEntryViewModel.deleteFirst(entryId.toLong())
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}