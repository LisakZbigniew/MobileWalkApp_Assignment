package uk.ac.shef.oak.com4510.ui.paths

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import uk.ac.shef.oak.com4510.R
import uk.ac.shef.oak.com4510.db.Photo
import uk.ac.shef.oak.com4510.db.relation.FullPath
import uk.ac.shef.oak.com4510.ui.gallery.grid.mAdapter
import java.text.DateFormat
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.exists

class PathAdapter : RecyclerView.Adapter<PathAdapter.ViewHolder> {

    lateinit var context: Context

    constructor(items: List<FullPath>) : super() {
        PathAdapter.items = items as MutableList<FullPath>
    }

    constructor(cont: Context, items: List<FullPath>) : super() {

        PathAdapter.items = items as MutableList<FullPath>

        context = cont
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //Inflate the layout, initialize the View Holder
        val v: View = LayoutInflater.from(parent.context).inflate(
            R.layout.path_details_item,
            parent, false
        )

        val holder: PathAdapter.ViewHolder = ViewHolder(v)
        context = parent.context
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Use the provided View Holder on the onCreateViewHolder method to populate the
        // current row on the RecyclerView
        holder.bindView(position)
        val recyclerView = holder.grid
        val numberOfColumns = 3
        recyclerView.layoutManager = GridLayoutManager(context, numberOfColumns)
        recyclerView.adapter = PhotoAdapter(items[position].photos) as RecyclerView.Adapter<RecyclerView.ViewHolder>


    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.clearView()
    }


    override fun getItemCount(): Int {
        return items.size
    }

    fun updateDataSet(newSet: MutableList<FullPath>){
        items = newSet as MutableList<FullPath>
        notifyDataSetChanged()
    }

    /**
     * A Viewholder for the elements code seting up maps adapted from :
     * https://github.com/googlemaps/android-samples/blob/master/ApiDemos/kotlin/app/src/gms/java/com/example/kotlindemos/LiteListDemoActivity.kt
     */
    inner class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view),
        OnMapReadyCallback {

        private val layout: View = view
        private val mapView: MapView = layout.findViewById(R.id.pathMap)
        private val title: TextView = layout.findViewById(R.id.pathTitle)
        private val time : TextView = layout.findViewById(R.id.pathTime)
        public val grid : RecyclerView = layout.findViewById(R.id.photoGrid)
        private lateinit var map: GoogleMap
        private lateinit var path: FullPath


        init {
            with(mapView) {
                // Initialise the MapView
                onCreate(null)
                // Set the map ready callback to receive the GoogleMap object
                getMapAsync(this@ViewHolder)
                setOnClickListener {  }
            }
        }

        private fun setMapLocation() {
            if (!::map.isInitialized) return
            if (!::path.isInitialized) return
            with(map) {
                path.drawPath(this)
                mapType = GoogleMap.MAP_TYPE_NORMAL

            }
        }

        override fun onMapReady(googleMap: GoogleMap) {
            MapsInitializer.initialize(context)
            // If map is not initialised properly
            map = googleMap
            map.uiSettings.isMapToolbarEnabled = false
            setMapLocation()
        }

        fun bindView(position: Int) {
            items[position].let {
                path = it
                mapView.tag = this
                title.text = it.path.title
                time.text = "Taken on ${it.getTime()}"
                // We need to call setMapLocation from here because RecyclerView might use the
                // previously loaded maps
                setMapLocation()
            }
        }

        fun clearView() {
            with(map) {
                // Clear the map and free up resources by changing the map type to none
                clear()
                mapType = GoogleMap.MAP_TYPE_NONE
            }
        }
    }



    companion object {
        lateinit var items: MutableList<FullPath>
        private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    }


}