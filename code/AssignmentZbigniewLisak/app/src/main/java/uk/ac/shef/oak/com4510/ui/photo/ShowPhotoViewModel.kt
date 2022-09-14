package uk.ac.shef.oak.com4510.ui.photo

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import uk.ac.shef.oak.com4510.PathsDB
import uk.ac.shef.oak.com4510.db.Photo
import java.text.SimpleDateFormat
import java.util.*

class ShowPhotoViewModel(application: Application) : AndroidViewModel(application) {

    private var photoId = -1L
    private val db by lazy { PathsDB(getApplication<Application>()) }
    private var photo: Photo? = null

    suspend fun setId(id: Long){
        photoId = id
        photo = db.getPhoto(id)
    }

    suspend fun getPhotoBitmap() : Bitmap {
        val uri = photo!!.uri
        return BitmapFactory.decodeFile(uri)
    }

    suspend fun getPhotoMeta() : Map<String,String> {
        val result = mutableMapOf<String,String>()
        result["Path"] = photo!!.uri
        result["Description"] = photo!!.description ?: "---"
        val df = SimpleDateFormat("EEE, dd MMM yyyy HH:mm")
        result["Date taken"] = df.format(Date(photo!!.datetime))

        if(photo!!.pathId != null){
            val path = photo!!.pathId?.let { db.getPathData(it) }
            result["Path title"] = path?.title ?: "---"
            val lastMeasurement = db.getLastMeasurement(photoId)
            if(lastMeasurement != null){
                result["Temperature"] = lastMeasurement.temperature.toString()
                result["Pressure"] = lastMeasurement.pressure.toString()
            }

        }
        return result
    }

}