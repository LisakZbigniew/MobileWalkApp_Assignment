package uk.ac.shef.oak.com4510.ui.paths

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import uk.ac.shef.oak.com4510.PathsDB
import uk.ac.shef.oak.com4510.db.relation.FullPath
import uk.ac.shef.oak.com4510.db.relation.PathDataWithPhotos

class PathsViewModel(application: Application) : AndroidViewModel(application) {

    private val db by lazy { PathsDB(getApplication<Application>()) }

    fun getFullPaths(): LiveData<List<FullPath>>? {
        return db.getFullPaths()
    }
}