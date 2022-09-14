package uk.ac.shef.oak.com4510.ui.paths

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import uk.ac.shef.oak.com4510.databinding.FragmentGalleryGridBinding
import uk.ac.shef.oak.com4510.databinding.FragmentPathsBinding
import uk.ac.shef.oak.com4510.db.Photo
import uk.ac.shef.oak.com4510.db.relation.FullPath
import uk.ac.shef.oak.com4510.db.relation.PathDataWithPhotos
import uk.ac.shef.oak.com4510.ui.gallery.grid.GalleryGridViewModel
import uk.ac.shef.oak.com4510.ui.gallery.grid.mAdapter

/**
 * Fragment presenting a list of paths
 */
class PathsFragment : Fragment() {

    private lateinit var myDataset: List<FullPath>
    private lateinit var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    private lateinit var recyclerView: RecyclerView

    private lateinit var pathsViewModel: PathsViewModel
    private var _binding: FragmentPathsBinding? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        pathsViewModel =
            ViewModelProvider(this).get(PathsViewModel::class.java)
        _binding = FragmentPathsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        myDataset = mutableListOf<FullPath>()

        recyclerView = binding.pathsRecyclerView
        // set up the RecyclerView
        val numberOfColumns = 4
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())
        adapter = PathAdapter(requireContext(),myDataset) as RecyclerView.Adapter<RecyclerView.ViewHolder>
        recyclerView.adapter = adapter

        pathsViewModel.getFullPaths()!!.observe(viewLifecycleOwner){
            (recyclerView.adapter as PathAdapter).updateDataSet((it as MutableList<FullPath>))
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}