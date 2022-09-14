package uk.ac.shef.oak.com4510.util

import com.google.android.gms.maps.model.LatLng

/**
 * Own class representing the location
 */
data class Location (
    val longitude: Float,
    val latitude: Float
        ){
    fun get(): LatLng{
        return LatLng(latitude.toDouble(), longitude.toDouble())
    }
}