package uk.ac.shef.oak.com4510.ui.gallery


import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import uk.ac.shef.oak.com4510.ui.gallery.grid.*
import uk.ac.shef.oak.com4510.ui.gallery.map.*


class GalleryPagerAdapter(f: Fragment) : FragmentStateAdapter(f) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> GalleryGridFragment();
            1 -> GalleryMapFragment();
            else -> GalleryGridFragment();
        }
    }

}