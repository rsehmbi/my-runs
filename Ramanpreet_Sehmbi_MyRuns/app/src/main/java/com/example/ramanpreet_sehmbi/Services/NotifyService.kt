package com.example.ramanpreet_sehmbi.Services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ContentProvider
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.*
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.ramanpreet_sehmbi.Automatic
import com.example.ramanpreet_sehmbi.R
import com.google.android.gms.maps.model.LatLng
import java.util.*
import java.util.concurrent.ArrayBlockingQueue

class NotifyService: Service(), LocationListener, SensorEventListener {
    private lateinit var notificationManager: NotificationManager
    private val CHANNEL_ID = "LOCATION CHANEL"
    var NOTIFY_ID = 1

    private lateinit var myBinder: Binder
    private lateinit var locationManager: LocationManager
    private var serviceGPSHandler: Handler? = null

    private var counter = 0;
    private lateinit var timer: Timer
    private lateinit var myTimerTask: TimerTask

    private lateinit var mAccBuffer: ArrayBlockingQueue<Double>
    private lateinit var mAsyncTask: OnSensorChangedTask
    private lateinit var mSensorManager: SensorManager
    private lateinit var mAccelerometer: Sensor

    companion object{
        val LATITUDE_LOCATION_KEY = "LATITUDE_LOCATION_KEY"
        val LONGITUDE_LOCATION_KEY = "LONGITUDE_LOCATION_KEY"
        val CURRENT_SPEED_KEY = "CURRENT_SPEED_KEY"
        val CURRENT_ALTITUDE_KEY = "CURRENT_ALTITUDE_KEY"
        val TIME_ELAPSED = "CURRENT_ALTITUDE_KEY"
        val PREDICTION_KEY = "PREDICTION"
    }

    override fun onCreate() {
        super.onCreate()
        myBinder = MyBinder()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        showNotification()
        initLocationManager()

        timer = Timer()
        myTimerTask = MyTimerTask()
        timer.scheduleAtFixedRate(myTimerTask, 0, 1000L)

        mAccBuffer = ArrayBlockingQueue<Double>(Globals.ACCELEROMETER_BUFFER_CAPACITY)

        mAsyncTask = OnSensorChangedTask()
        mAsyncTask.execute()
    }

    inner class MyTimerTask: TimerTask(){
        override fun run() {
            counter++
        }
    }

    override fun onLocationChanged(location: Location){
        val lat = location.latitude
        val lng = location.longitude
        sendLatLongMessage(lat, lng, location.speed.toString(), location.altitude)
    }

    fun sendLatLongMessage(lat:Double, lng: Double, speed: String, altitude: Double){
        if(serviceGPSHandler != null){
            val bundle = Bundle()
            bundle.putDouble(LATITUDE_LOCATION_KEY, lat)
            bundle.putDouble(LONGITUDE_LOCATION_KEY, lng)
            bundle.putString(CURRENT_SPEED_KEY, speed.toString())
            bundle.putDouble(CURRENT_ALTITUDE_KEY, altitude)
            bundle.putInt(TIME_ELAPSED, counter)

            val message = serviceGPSHandler!!.obtainMessage()
            message.data = bundle
            serviceGPSHandler!!.sendMessage(message)
        }
    }

    fun sendPrediction(prediction: Int){
        if(serviceGPSHandler!=null){
            val bundle = Bundle()
            bundle.putInt(PREDICTION_KEY, prediction)
            val message = serviceGPSHandler!!.obtainMessage()
            message.data = bundle
            serviceGPSHandler!!.sendMessage(message)
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

    override fun onBind(intent: Intent?): IBinder? {
        return myBinder
    }

    inner class MyBinder: Binder(){
        fun setgpsMessageHandler(inputHandler: Handler){
            serviceGPSHandler = inputHandler
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationManager.cancel(NOTIFY_ID)
        if (locationManager != null){
            locationManager.removeUpdates(this)
        }
        if(timer != null){
            timer.cancel()
        }
        counter = 0

        mAsyncTask.cancel(true)
        try {
            Thread.sleep(100)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST)
        return START_NOT_STICKY
    }

    override fun onUnbind(intent: Intent?): Boolean {
        serviceGPSHandler = null
        return true
    }

    private fun showNotification(){
        val notificationCompactBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
        notificationCompactBuilder.setContentTitle("My Runs")
        notificationCompactBuilder.setContentText("Your activity is being recorded")
        notificationCompactBuilder.setSmallIcon(R.drawable.run)

        val resultIntent = Intent(this, Automatic::class.java)
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        val pendingIntent = PendingIntent.getActivity(this, 3434, resultIntent, PendingIntent.FLAG_IMMUTABLE)
        notificationCompactBuilder.setContentIntent(pendingIntent)

        val notification = notificationCompactBuilder.build()

        if(Build.VERSION.SDK_INT > 26){
            val notificationChannel = NotificationChannel(CHANNEL_ID, "My Runs GPS", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(NOTIFY_ID, notification)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) {
            val m = Math.sqrt((event.values[0] * event.values[0] + event.values[1] * event.values[1] + (event.values[2]
                    * event.values[2])).toDouble())
            try {
                mAccBuffer.add(m)
            } catch (e: IllegalStateException) {
                val newBuf = ArrayBlockingQueue<Double>(mAccBuffer.size * 2)
                mAccBuffer.drainTo(newBuf)
                mAccBuffer = newBuf
                mAccBuffer.add(m)
            }
        }
    }

    inner class OnSensorChangedTask : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg arg0: Void?): Void? {
            var blockSize = 0
            val fft = FFT(Globals.ACCELEROMETER_BLOCK_CAPACITY)
            val accBlock = DoubleArray(Globals.ACCELEROMETER_BLOCK_CAPACITY)
            val im = DoubleArray(Globals.ACCELEROMETER_BLOCK_CAPACITY)
            var max = Double.MIN_VALUE
            while (true) {
                try {
                    // need to check if the AsyncTask is cancelled or not in the while loop
                    if (isCancelled() == true) {
                        return null
                    }

                    // Dumping buffer
                    accBlock[blockSize++] = mAccBuffer.take().toDouble()
                    if (blockSize == Globals.ACCELEROMETER_BLOCK_CAPACITY) {
                        blockSize = 0

                        // time = System.currentTimeMillis();
                        max = .0
                        for (`val` in accBlock) {
                            if (max < `val`) {
                                max = `val`
                            }
                        }
                        fft.fft(accBlock, im)
                        for (i in accBlock.indices) {
                            val mag = Math.sqrt(accBlock[i] * accBlock[i] + im[i]
                                    * im[i])
                            im[i] = .0 // Clear the field
                        }
                        sendPrediction(WekaClassifier.classify(accBlock.toTypedArray()).toInt())
//                        println("The data is " + WekaClassifier.classify(accBlock.toTypedArray()).toString())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
}

