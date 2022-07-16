package com.example.ramanpreet_sehmbi.Services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.*
import android.net.Uri
import android.os.*
import androidx.core.app.NotificationCompat
import com.example.ramanpreet_sehmbi.R
import com.example.ramanpreet_sehmbi.ViewModels.GPSViewModel
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class NotifyService: Service(), LocationListener {
    private lateinit var notificationManager: NotificationManager
    private val CHANNEL_ID = "LOCATION CHANEL"
    var NOTIFY_ID = 1
    private val web = "https://www.google.com"
    private lateinit var myBroadcastReceiver: BroadcastReceiver;


    private lateinit var myBinder: Binder
    private lateinit var locationManager: LocationManager
    private var serviceGPSHandler: Handler? = null

    companion object{
        val LATITUDE_LOCATION_KEY = "LATITUDE_LOCATION_KEY"
        val LONGITUDE_LOCATION_KEY = "LONGITUDE_LOCATION_KEY"
    }

    override fun onCreate() {
        super.onCreate()
        println("raman debug: on Create()")
        myBinder = MyBinder()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        showNotification()


        myBroadcastReceiver = MyBroadcastReceiver()
        initLocationManager()
        val intentFilter = IntentFilter()
        intentFilter.addAction("STOP_RUN")
        registerReceiver(myBroadcastReceiver, intentFilter)
    }

    override fun onLocationChanged(location: Location){
        val lat = location.latitude
        val lng = location.longitude
        val currentLocation = LatLng(lat, lng)
        printLocationonConsole(lat, lng)
        sendLatLongMessage(lat, lng)
    }

    fun sendLatLongMessage(lat:Double, lng: Double){
        if(serviceGPSHandler!=null){
            val bundle = Bundle()
            bundle.putDouble(LATITUDE_LOCATION_KEY, lat)
            bundle.putDouble(LONGITUDE_LOCATION_KEY, lng)

            val message = serviceGPSHandler!!.obtainMessage()
            message.data = bundle
            serviceGPSHandler!!.sendMessage(message)
        }
    }

    fun printLocationonConsole(lat: Double, lng: Double){
        var line2 = ""
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(lat, lng, 1)
        val address = addresses.get(0)

        for (i in 0..address.maxAddressLineIndex)
            line2 += "${address.getAddressLine(i)}\n"

        println("raman debug: The last inside Notify Service "+ line2 )

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

    override fun onBind(intent: Intent?): IBinder? {
        println("raman debug: on Bind()")
        return myBinder
    }

    inner class MyBinder: Binder(){
        fun setgpsMessageHandler(inputHandler: Handler){
            serviceGPSHandler = inputHandler
        }
    }

    override fun onDestroy() {
        println("raman debug: on Destroy()")
        super.onDestroy()
        notificationManager.cancel(NOTIFY_ID)
        if (locationManager != null){
            locationManager.removeUpdates(this)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("raman debug: on Start()")
        return START_NOT_STICKY
    }

    override fun onUnbind(intent: Intent?): Boolean {
        println("raman debug: onUnbind()")
        serviceGPSHandler = null
        return true
    }

    private fun showNotification(){
        val notificationCompactBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
        notificationCompactBuilder.setContentTitle("My Runs")
        notificationCompactBuilder.setContentText("Your activity is being recorded")
        notificationCompactBuilder.setSmallIcon(R.drawable.run)
        notificationCompactBuilder.setAutoCancel(true)


        val webpageIntent = Intent(Intent.ACTION_VIEW, Uri.parse(web))
        val pendingIntent = PendingIntent.getActivity(this, 3434, webpageIntent, PendingIntent.FLAG_IMMUTABLE)
        notificationCompactBuilder.setContentIntent(pendingIntent)

        val notification = notificationCompactBuilder.build()

        if(Build.VERSION.SDK_INT > 26){
            val notificationChannel = NotificationChannel(CHANNEL_ID, "My Runs GPS", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(NOTIFY_ID, notification)
    }

    inner class MyBroadcastReceiver: BroadcastReceiver(){
        override fun onReceive(context: Context?, Intent: Intent?) {
            notificationManager.cancel(NOTIFY_ID)
            stopSelf()
            unregisterReceiver(myBroadcastReceiver)
        }

    }

}

