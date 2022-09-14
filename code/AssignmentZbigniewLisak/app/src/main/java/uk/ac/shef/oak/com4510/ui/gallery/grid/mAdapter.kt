package uk.ac.shef.oak.com4510.ui.gallery.grid

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import uk.ac.shef.oak.com4510.R
import uk.ac.shef.oak.com4510.ShowPhoto
import uk.ac.shef.oak.com4510.db.Photo
import kotlin.io.path.Path
import kotlin.io.path.exists

class mAdapter : RecyclerView.Adapter<mAdapter.ViewHolder> {

    private lateinit var context: Context

    constructor(items: List<Photo>) : super() {
        mAdapter.items = items as MutableList<Photo>
    }

    constructor(cont: Context, items: List<Photo>) : super() {

        mAdapter.items = items as MutableList<Photo>

        context = cont
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //Inflate the layout, initialize the View Holder
        val v: View = LayoutInflater.from(parent.context).inflate(
            R.layout.list_image_item,
            parent, false
        )

        val holder: mAdapter.ViewHolder = ViewHolder(v)
        context = parent.context
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Use the provided View Holder on the onCreateViewHolder method to populate the
        // current row on the RecyclerView

        if (Path(items[position].thumbnailUri).exists()) {

            scope.launch(Dispatchers.Main){
                val myBitmap = items[position].thumbnail
                holder.imageView.setImageBitmap(myBitmap)
            }

            holder.itemView.setOnClickListener(View.OnClickListener {
                val intent = Intent(context, ShowPhoto::class.java)
                intent.putExtra("photoId", items[position].id)
                context.startActivity(intent)
            })

        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateDataSet(newSet: MutableList<Photo>){
        items = newSet as MutableList<Photo>
        notifyDataSetChanged()
    }

    class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById<View>(R.id.image_item) as ImageView
    }


    companion object {
        lateinit var items: MutableList<Photo>
        private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    }


}