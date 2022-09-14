package uk.ac.shef.oak.com4510.ui.gallery.grid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pl.aprilapps.easyphotopicker.R
import uk.ac.shef.oak.com4510.PathsDB
import uk.ac.shef.oak.com4510.databinding.FragmentGalleryGridBinding
import uk.ac.shef.oak.com4510.db.Photo

import java.util.ArrayList

/**
 * Fragment for a grid of photos in the gallery
 */

class GalleryGridFragment : Fragment() {

    private lateinit var myDataset: List<Photo>
    private lateinit var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    private lateinit var recyclerView: RecyclerView

        private lateinit var galleryGridViewModel: GalleryGridViewModel
        private var _binding: FragmentGalleryGridBinding? = null


        // This property is only valid between onCreateView and
        // onDestroyView.
        private val binding get() = _binding!!

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {

            galleryGridViewModel = ViewModelProvider(this).get(GalleryGridViewModel::class.java)

            _binding = FragmentGalleryGridBinding.inflate(inflater, container, false)
            val root: View = binding.root

            myDataset = mutableListOf<Photo>()

            recyclerView = binding.gridRecyclerView

            // set up the RecyclerView
            val numberOfColumns = 4
            recyclerView.layoutManager = GridLayoutManager(this.requireContext(), numberOfColumns)
            adapter = mAdapter(myDataset) as RecyclerView.Adapter<RecyclerView.ViewHolder>
            recyclerView.adapter = adapter

            //observe the photos in db
            galleryGridViewModel.getExistingPhotos()!!.observe(viewLifecycleOwner){
                (recyclerView.adapter as mAdapter).updateDataSet((it as MutableList<Photo>))
            }

            return root
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
}