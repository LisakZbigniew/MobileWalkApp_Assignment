package uk.ac.shef.oak.com4510

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.loader.app.LoaderManager
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.Channel
import uk.ac.shef.oak.com4510.db.Measurement
import uk.ac.shef.oak.com4510.util.*
import java.lang.Exception
import java.util.*

/**
 * A foreground service tracking a path by taking measurements and recording them in the db
 */

class TrackPathService : Service(){

    private var pathId = -1L
    private val db by lazy { PathsDB(application) }

    private lateinit var barometer : Barometer
    private lateinit var thermometer : Thermometer

    private lateinit var locationClient : FusedLocationProviderClient
    private val locationCallback by lazy { locationCallback() }

    override fun onCreate() {
        createNotificationChannel()
        barometer = Barometer(this)
        thermometer = Thermometer(this)
        locationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        pathId = intent.getLongExtra("pathId",-1L)
        foreground()
        if(pathId != -1L){
            thermometer.startThermometerSensing()
            barometer.startBarometerSensing()

            checkPermissions()
            locationClient.requestLocationUpdates(getLocationRequest(), locationCallback, Looper.getMainLooper())

        }
        return START_REDELIVER_INTENT
    }

    private fun foreground(){
        val intent = Intent(this, TrackPathActivity::class.java)
        intent.putExtra("pathId",pathId)
        val pendingIntent =  PendingIntent.getActivity(this, 263, intent, PendingIntent.FLAG_UPDATE_CURRENT)


        val notification: Notification = Notification.Builder(this,  "Tracking")
            .setContentTitle(getText(R.string.notification_title))
            .setContentText(getText(R.string.notification_message))
            .setSmallIcon(R.drawable.route)
            .setContentIntent(pendingIntent)
            .setTicker(getText(R.string.ticker_text))
            .build()

        startForeground(1, notification)

    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("Tracking", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

    }

    private fun checkPermissions(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this,"Location required",Toast.LENGTH_SHORT).show()
        }
    }

    private fun getLocationRequest():LocationRequest{
        return LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun locationCallback():LocationCallback{
        return object : LocationCallback(){
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                p0.locations.forEach {
                    if (it != null) {

                        val newMeasurement = Measurement(
                            datetime = it.time,
                            location = Location(it.longitude.toFloat(),it.latitude.toFloat()),
                            temperature = thermometer.temperatureReading.value?.toInt()?:0,
                            pressure = barometer.pressureReading.value?:0.0f,
                            pathId = pathId
                        )
                        db.newMeasurement(newMeasurement)
                    }
                }

            }
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        // We don't provide binding, so return null
        return null
    }

    override fun onDestroy() {
        thermometer.stopThermometerSensing()
        barometer.stopBarometerSensing()
        locationClient.removeLocationUpdates(locationCallback)
        pathId = -1
        Toast.makeText(this, "Path tracking finished", Toast.LENGTH_SHORT).show()
    }

    companion object{
        private var INSTANCE : TrackPathService? = null
        public fun getService() : TrackPathService{
            return TrackPathService.INSTANCE ?: synchronized(this) {
                val instance = TrackPathService()
                INSTANCE = instance
                return instance
            }
        }
    }
}
