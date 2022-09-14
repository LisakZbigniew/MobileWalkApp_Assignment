/*
 * Copyright (c) 2019. This code has been developed by Fabio Ciravegna, The University of Sheffield.
 * Updated 2021 by Temitope Adeosun, using Kotlin with MVVM and LiveData implementation
 * All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package uk.ac.shef.oak.com4510.util

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.MutableLiveData
import java.lang.Exception

/**
 * A barometer sensor for collecting pressure
 */
class Barometer(context: Context) {
    private val BAROMETER_READING_FREQ_MICRO_SEC: Int = 30000
    private var samplingRateInMicroSec: Long = BAROMETER_READING_FREQ_MICRO_SEC.toLong()
    private var samplingRateInNanoSec: Long = samplingRateInMicroSec * 1000
    private var timePhoneWasLastRebooted: Long = 0
    private var lastReportTime: Long = 0

    private var sensorManager: SensorManager?
    private var barometerSensor: Sensor
    private var barometerEventListener: SensorEventListener? = null
    private var _isStarted = false
    val isStarted: Boolean
        get() {return _isStarted}

    var pressureReading: MutableLiveData<Float> = MutableLiveData<Float>()


    init{
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        barometerSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_PRESSURE)!!

        /**
         * this inits the listener and establishes the actions to take when a sensor is available
         * It is not registere to listen at this point, but makes sure the object is available to
         * listen when registered.
         */
        barometerEventListener  = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val diff = event.timestamp - this@Barometer.lastReportTime
                // time is in nanoseconds it represents the set reference times the first time we come here
                // set event timestamp to current time in milliseconds
                // see answer 2 at http://stackoverflow.com/questions/5500765/accelerometer-sensorevent-timestamp
                // the following operation avoids reporting too many events too quickly - the sensor may always
                // misbehave and start sending data very quickly

                if (diff >= this@Barometer.samplingRateInNanoSec) {
                    val actualTimeInMseconds =
                        this@Barometer.timePhoneWasLastRebooted + (event.timestamp / 1000000.0).toLong()
                    if(pressureReading.value != event.values[0]){pressureReading.value = event.values[0]}
                    this@Barometer.lastReportTime = event.timestamp
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    companion object {
        private val TAG = Barometer::class.java.simpleName
    }

    /**
     * it starts the pressure monitoring and updates the _isStarted status flag
     * @param accelerometer
     */
    fun startBarometerSensing() {
        sensorManager?.also {
            if(barometerSensor == null) return
            Log.d(TAG, "Starting listener")
            it.registerListener(
                barometerEventListener,
                barometerSensor,
                samplingRateInMicroSec.toInt()
            )
            _isStarted = true
        }
    }

    /**
     * this stops the barometer and updates the _isStarted status flag
     */
    fun stopBarometerSensing() {
        sensorManager?.let {
            Log.d(TAG, "Stopping listener")
            try {
                it.unregisterListener(barometerEventListener)
                _isStarted = false
            } catch (e: Exception) {
                // probably already unregistered
//                Log.d(Accelerometer.TAG, "failed to unregister sensor, probably not running already")
            }
        }
    }


}