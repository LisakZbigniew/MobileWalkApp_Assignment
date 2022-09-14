package uk.ac.shef.oak.com4510.db.relation

import android.graphics.Bitmap
import android.util.Log
import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import uk.ac.shef.oak.com4510.R
import uk.ac.shef.oak.com4510.db.Measurement
import uk.ac.shef.oak.com4510.db.PathData
import uk.ac.shef.oak.com4510.db.Photo
import java.text.SimpleDateFormat
import java.util.*
/**
 * Class representing a path with both all the measurements and its photos
 */
data class FullPath(
    @Embedded var path: PathData,
    @Relation(
        entity = Measurement::class,
        parentColumn = "id",
        entityColumn = "path_id"
    )
    var measurements: List<Measurement>,
    @Relation(
        entity = Photo::class,
        parentColumn = "id",
        entityColumn = "path_id"
        )
    var photos: List<Photo>,

){
    /**
     * Function calculates and formats time frame in which a path took place using its measurements
     * @return String formated "$start - $end"
     */

    public fun getTime(): String {
        var first: Measurement? = measurements.firstOrNull()
        var last: Measurement? = measurements.firstOrNull()
        if(first == null) return "---"

        measurements.forEach {
            if(it.datetime>last?.datetime?:0) last = it
            if(it.datetime<first?.datetime?:0) first = it
        }

        val df = SimpleDateFormat("EEE, dd MMM yyyy HH:mm")
        val from = df.format(Date(first?.datetime?:0))
        val to = df.format(Date(last?.datetime?:0))
        return "$from - $to"
    }

    /**
     * Given a map draw a polyline representing a path and center it in the view
     * @param map map to draw onto
     */

    fun drawPath(map: GoogleMap) {
        map.clear()

        if (measurements.isEmpty()){
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(53.38,-1.46),12.0f))
            return
        }
        if (measurements.size == 1){
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(measurements[0].location.get(),12.0f))
            return
        }
        val options = PolylineOptions().color(2133100751).jointType(JointType.ROUND)
        map.addPolyline(options).points = measurements.map{it.location.get()}
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds(),10))
    }

    /**
     * Calculate bounding box of current measurements
     */

    private fun bounds() : LatLngBounds{
        val builder = LatLngBounds.builder()
        measurements.forEach { builder.include(it.location.get()) }
        return builder.build()
    }

    /**
     * Given a map draw it's photos on it using markers
     */

    fun drawPhotos(map: GoogleMap){
        for (photo in photos) {
            val marker = map.addMarker(MarkerOptions()
                .position(photo.location.get())
                .icon(icon(photo)))
            marker?.tag = photo
        }
    }

    /**
     *   Using thumbnail create scaled bitmap for the icon on the map
     */

    private fun icon(photo : Photo) : BitmapDescriptor{
        val unScaledBitmap = photo.thumbnail
        val scale = 0.5
        val scaledBitmap = Bitmap.createScaledBitmap(
            unScaledBitmap,
            (unScaledBitmap.width * scale).toInt(), (unScaledBitmap.height * scale).toInt(), false
        )
        return BitmapDescriptorFactory.fromBitmap(scaledBitmap)
    }

}