package uk.ac.shef.oak.com4510

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.aprilapps.easyphotopicker.MediaFile
import uk.ac.shef.oak.com4510.PathsDB
import uk.ac.shef.oak.com4510.db.Photo
import uk.ac.shef.oak.com4510.db.relation.FullPath
import uk.ac.shef.oak.com4510.util.Location

class TrackPathViewModel(application: Application) : AndroidViewModel(application) {

    private val db by lazy{PathsDB(application)}

    public fun getFullPath(id : Long): LiveData<FullPath>? {
        return db.getFullPath(id)
    }

    /**
     * given a list of files it creates photos and
     * adds them to database
     * @param returnedPhotos
     * @return
     */
    public fun newPhotos(returnedPhotos: Array<MediaFile>,path : FullPath) {
        val latestLocation = path.measurements.maxByOrNull { it.datetime }?.location?: Location(0.0f,0.0f)
        for (mediaFile in returnedPhotos) {
            viewModelScope.launch(Dispatchers.IO){
                val photo = Photo.build(mediaFile)
                photo.pathId = path.path.id
                photo.location = latestLocation
                db.newPhoto(photo)
            }
        }


    }


}