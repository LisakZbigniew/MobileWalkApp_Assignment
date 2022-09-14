package uk.ac.shef.oak.com4510.ui.gallery.map

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.ac.shef.oak.com4510.R
import uk.ac.shef.oak.com4510.ShowPhoto
import uk.ac.shef.oak.com4510.databinding.FragmentGalleryMapBinding
import uk.ac.shef.oak.com4510.db.Photo

/**
 * Fragment for a map displaying photos in the gallery
 */

class GalleryMapFragment : Fragment() {
    private lateinit var myDataset: List<Photo>

    private lateinit var galleryMapViewModel: GalleryMapViewModel
    private var _binding: FragmentGalleryMapBinding? = null
    private var map : GoogleMap? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        galleryMapViewModel = ViewModelProvider(this).get(GalleryMapViewModel::class.java)

        _binding = FragmentGalleryMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        myDataset = mutableListOf<Photo>()

        binding.galleryMap.getMapAsync {
            //set up photos when map ready
            map = it
            addMarkers()
            map!!.setOnMarkerClickListener {
                val intent = Intent(context, ShowPhoto::class.java)
                intent.putExtra("photoId", (it.tag as Photo).id)
                requireContext().startActivity(intent)
                true
            }
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(53.38,-1.46),12.0f))

        }
        binding.galleryMap.onCreate(null)

        //start observing the list ofphotos
        galleryMapViewModel.getExistingPhotos()!!.observe(viewLifecycleOwner){
            myDataset = it
            if(map!= null) {
                map?.clear()
                addMarkers()
            }
        }

        return root
    }

    private fun addMarkers(){
        for (photo in myDataset) {
            val marker = map?.addMarker(MarkerOptions()
                                        .position(photo.location.get())
                                        .icon(icon(photo)))
            marker?.tag = photo
        }
    }

    private fun icon(photo : Photo) : BitmapDescriptor{
        val unScaledBitmap = photo.thumbnail
        val scale = 0.5
        val scaledBitmap = Bitmap.createScaledBitmap(
            unScaledBitmap,
            (unScaledBitmap.width * scale).toInt(), (unScaledBitmap.height * scale).toInt(), false
        )
        return BitmapDescriptorFactory.fromBitmap(scaledBitmap)
    }

    override fun onPause() {
        super.onPause()
        binding.galleryMap.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding.galleryMap.onResume()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding.galleryMap.onDestroy()
        map = null
        _binding = null
    }
}