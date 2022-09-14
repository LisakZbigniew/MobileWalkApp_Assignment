package uk.ac.shef.oak.com4510.ui.gallery.grid

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import uk.ac.shef.oak.com4510.PathsDB
import uk.ac.shef.oak.com4510.db.Photo


class GalleryGridViewModel(application: Application) : AndroidViewModel(application) {

    private val db by lazy { PathsDB(getApplication<Application>()) }

    fun getExistingPhotos(): LiveData<List<Photo>>? {
        return db.getPhotos()
    }
}