package uk.ac.shef.oak.com4510.ui.home

import android.app.Application
import android.os.SystemClock
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.aprilapps.easyphotopicker.MediaFile
import uk.ac.shef.oak.com4510.PathsDB
import uk.ac.shef.oak.com4510.db.*
import uk.ac.shef.oak.com4510.db.relation.PathDataWithMeasurements
import uk.ac.shef.oak.com4510.util.*

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val db by lazy { PathsDB(getApplication<Application>()) }


    suspend fun addNewPath(title : String):Long{
        return db.newPath(PathData(title = title))
    }



    /**
     * given a list of files it creates photos and
     * adds them to database
     * @param returnedPhotos
     * @return
     */
    public fun newPhotos(returnedPhotos: Array<MediaFile>) {

        for (mediaFile in returnedPhotos) {
            viewModelScope.launch(Dispatchers.IO){
                db.newPhoto(Photo.build(mediaFile))
            }
        }

    }



}