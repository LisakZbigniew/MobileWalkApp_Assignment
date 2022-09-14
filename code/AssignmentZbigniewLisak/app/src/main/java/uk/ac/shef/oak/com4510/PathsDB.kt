package uk.ac.shef.oak.com4510


import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.ac.shef.oak.com4510.db.*
import uk.ac.shef.oak.com4510.db.relation.*
import uk.ac.shef.oak.com4510.util.*
import pl.aprilapps.easyphotopicker.*
import uk.ac.shef.oak.com4510.db.relation.FullPath

/**
 * An entry point for calling the DAO object
 */

class PathsDB(application: Application): ViewModel() {

    private var dao: PathDao? = null
    init {
        //Init DB
        val db: Paths? = Paths.getDatabase(application)
        if (db != null) { dao = db.pathDao() }
    }

    public fun getFullPaths(): LiveData<List<FullPath>>? {
        return dao?.getFullPaths()
    }

    public fun getFullPath(id:Long): LiveData<FullPath>? {
        return dao?.getFullPath(id)
    }

    public fun getPaths(): LiveData<List<PathDataWithMeasurements>>? {
        return dao?.getAllPathsWithMeasurements()
    }

    public fun getPathPhotos(): LiveData<List<PathDataWithPhotos>>? {
        return dao?.getAllPathsWithPhotos()
    }

    public fun getPhotos() : LiveData<List<Photo>>?{
        return dao?.getAllPhotos()
    }

    suspend fun getPhoto(id: Long) : Photo?{
        return dao?.getPhoto(id)
    }

    suspend fun getPathData(pathId: Long): PathData {
        return dao?.getPathData(pathId) ?: PathData(title = "")
    }

    suspend fun getLastMeasurement(photoId: Long) : Measurement?{

        return dao?.getPhotoMeasurement(photoId)
    }

    public fun newPath(path: PathDataWithMeasurements){
        viewModelScope.launch(Dispatchers.IO) {
            val pathData = path.path
            val id = dao?.insertPath(pathData)

            if (id != null) {
                path.measurements.forEach {
                    it.pathId = id
                    dao?.insertMeasurement(it)
                }
            }
        }
    }

    public fun newPhoto(photo: Photo){
        viewModelScope.launch(Dispatchers.IO){
            dao?.insertPhoto(photo)
        }
    }

    fun newMeasurement(measurement: Measurement) {
        viewModelScope.launch(Dispatchers.IO){
            dao?.insertMeasurement(measurement)
        }
    }
    suspend fun newPath(path : PathData): Long{
        return dao?.insertPath(path)?:-1L

    }


}