package uk.ac.shef.oak.com4510

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.*

import pl.aprilapps.easyphotopicker.*
import uk.ac.shef.oak.com4510.databinding.ActivityTrackPathBinding
import uk.ac.shef.oak.com4510.db.Photo
import uk.ac.shef.oak.com4510.db.relation.FullPath

/**
 * An activity for monitoring a process of path tracking
 */
class TrackPathActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityTrackPathBinding
    private lateinit var map: MapView
    private var path: FullPath? = null
    private val trackingService = TrackPathService.getService()
    private lateinit var trackViewModel: TrackPathViewModel
    private var easyImage : EasyImage? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = intent.getLongExtra("pathId", -1L)
        trackViewModel = ViewModelProvider(this).get(TrackPathViewModel::class.java)
        binding = ActivityTrackPathBinding.inflate(layoutInflater)
        setContentView(binding.root)

        easyImage =  initEasyImage(this)

        trackViewModel.getFullPath(id)!!.observe(this){
            path = it
            if(::mMap.isInitialized){
                mMap.clear()
                path!!.drawPath(mMap)
                path!!.drawPhotos(mMap)
            }
        }

        map = binding.map
        map.onCreate(null)
        map.getMapAsync(this)

        binding.stopButton.setOnClickListener {
            stopService(Intent(applicationContext,TrackPathService::class.java))
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.putExtra("destination",R.id.navigation_paths)
            startActivity(intent)
            finish()
        }

        binding.photoButton.setOnClickListener{
            easyImage!!.openChooser(this)
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMarkerClickListener {
            val intent = Intent(applicationContext, ShowPhoto::class.java)
            intent.putExtra("photoId", (it.tag as Photo).id)
            startActivity(intent)
            true
        }

        if(path != null){
            path?.drawPath(mMap)
            path?.drawPhotos(mMap)
        }
    }

    /**
     * it initialises EasyImage
     */
    private fun initEasyImage(context: Context) : EasyImage {
        return EasyImage.Builder(context)
            .setChooserTitle("Pick media")
            .setChooserType(ChooserType.CAMERA_AND_GALLERY)
            .allowMultiple(true)
            .setCopyImagesToPublicGalleryFolder(true)
            .build()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        easyImage?.handleActivityResult(requestCode, resultCode, data, this,
            object : DefaultCallback() {
                override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                    trackViewModel.newPhotos(imageFiles,path!!)
                    mMap.clear()
                    path!!.drawPath(mMap)
                    path!!.drawPhotos(mMap)
                }

                override fun onImagePickerError(error: Throwable, source: MediaSource) {
                    super.onImagePickerError(error, source)
                }

                override fun onCanceled(source: MediaSource) {
                    super.onCanceled(source)
                }
            })

    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        map.onDestroy()
    }

}