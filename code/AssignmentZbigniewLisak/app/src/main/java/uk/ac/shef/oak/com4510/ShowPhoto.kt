package uk.ac.shef.oak.com4510

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import uk.ac.shef.oak.com4510.ui.photo.ShowPhotoFragment

/**
 * An activity for showing full - sized photo  and its metadata
 */
class ShowPhoto : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.show_photo_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ShowPhotoFragment())
                .commitNow()
        }
    }
}