package uk.ac.shef.oak.com4510.ui.photo

import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uk.ac.shef.oak.com4510.R
import uk.ac.shef.oak.com4510.databinding.FragmentPathsBinding
import uk.ac.shef.oak.com4510.databinding.ShowPhotoFragmentBinding
import uk.ac.shef.oak.com4510.db.relation.FullPath
import uk.ac.shef.oak.com4510.ui.paths.PathAdapter
import uk.ac.shef.oak.com4510.ui.paths.PathsViewModel

/**
 * Fragment presenting a full-sized photos with some of its metadata
 */
class ShowPhotoFragment : Fragment() {

    private lateinit var showPhotoViewModel: ShowPhotoViewModel
    private var _binding: ShowPhotoFragmentBinding? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        showPhotoViewModel =
            ViewModelProvider(this).get(ShowPhotoViewModel::class.java)
        _binding = ShowPhotoFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val b: Bundle? = requireActivity().intent.extras
        var photoId = -1L

        if (b != null) {
            photoId = b.getLong("photoId")
            if (photoId != -1L) {
                lifecycleScope.launch(Dispatchers.IO){

                    showPhotoViewModel.setId(photoId)
                    val photo = showPhotoViewModel.getPhotoBitmap()
                    val meta = showPhotoViewModel.getPhotoMeta()
                    withContext(Dispatchers.Main) {
                        binding.photo.setImageBitmap(photo)
                        binding.photoUri.text = "Path : " + meta["Path"]
                        binding.photoDesc.text = "Description : " + meta["Description"]
                        binding.photoDate.text = "Date taken : " + meta["Date taken"]
                        binding.photoPathTitle.text = "Path title : " + (meta["Path title"] ?: "---")
                        binding.photoTemp.text = "Temperature : " + (meta["Temperature"] ?: "---") + "C"
                        binding.photoPressure.text = "Pressure : " + (meta["Pressure"] ?: "---") + " hPA"
                    }


                }
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}