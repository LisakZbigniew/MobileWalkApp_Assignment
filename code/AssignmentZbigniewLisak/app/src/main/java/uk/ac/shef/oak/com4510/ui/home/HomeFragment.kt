package uk.ac.shef.oak.com4510.ui.home

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import pl.aprilapps.easyphotopicker.*
import uk.ac.shef.oak.com4510.databinding.FragmentHomeBinding
import android.content.DialogInterface
import android.text.InputType

import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ShareCompat
import androidx.core.view.setPadding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.ac.shef.oak.com4510.*
import uk.ac.shef.oak.com4510.R

/**
 * Fragment to represent the main page with links to different functionalities
 */

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    private var easyImage : EasyImage? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        easyImage = this.activity?.let { initEasyImage(it) }

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.newPathButton.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("New Path")

            // Set up the input view for querying for path title
            val input = EditText(requireContext())
            input.inputType = InputType.TYPE_CLASS_TEXT
            input.setHint(R.string.set_title)
            input.setPadding(50)
            builder.setView(input)

            // Set up the buttons
            builder.setPositiveButton("Start Tracking"){dialog,which ->
                CoroutineScope(Dispatchers.IO).launch {
                    val id = homeViewModel.addNewPath(input.text.toString())
                    if (id != -1L) {
                        startTracking(id)
                    }
                }
            }
            builder.setNegativeButton("Cancel",
                DialogInterface.OnClickListener { dialog, which -> })

            builder.show()
        }


        binding.addPhotosButton.setOnClickListener {
            easyImage?.openChooser(this)
        }

        return root


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Launch service for tracking a path and a activity for monitoring it
     * @param id a valid path id in the DB, that is being tracked
     */
    private fun startTracking(id : Long){
        val service = TrackPathService.getService()
        val serviceIntent = Intent(context, TrackPathService::class.java)
        serviceIntent.putExtra("pathId", id)
        requireActivity().startForegroundService(serviceIntent)

        val activityIntent = Intent(context, TrackPathActivity::class.java)
        activityIntent.putExtra("pathId",id)
        requireActivity().startActivity(activityIntent)
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

        this.activity?.let {
            easyImage?.handleActivityResult(requestCode, resultCode,data, it,
                object: DefaultCallback() {
                    override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                        homeViewModel.newPhotos(imageFiles)
                        (activity as MainActivity).changePage(R.id.navigation_gallery)
                    }

                    override fun onImagePickerError(error: Throwable, source: MediaSource) {
                        super.onImagePickerError(error, source)
                    }

                    override fun onCanceled(source: MediaSource) {
                        super.onCanceled(source)
                    }
                })
        }
    }
}