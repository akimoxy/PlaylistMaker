package com.example.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track
import java.util.Locale

const val TWENTY_FIVE = 25

class TrackAdapter(
    private var listOfTrack: List<Track>, private var clickListener: (track: Track) -> Unit
) : RecyclerView.Adapter<TrackAdapter.SearchViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.track_item, parent, false,
        )
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(listOfTrack[position])
        holder.itemView.setOnClickListener {
            clickListener(listOfTrack[position])
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<Track>) {
        listOfTrack = newList
        this.notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return listOfTrack.size
    }

    inner class SearchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val artistNameView: TextView = itemView.findViewById(R.id.artist_name)
        private val trackNameView: TextView = itemView.findViewById(R.id.track_name)
        private val trackTimeView: TextView = itemView.findViewById(R.id.track_time)
        private val artworkUrlView: ImageView = itemView.findViewById(R.id.track_image_url)
        private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

        @SuppressLint("SetTextI18n")
        fun bind(item: Track) {
            if (item.artistName!!.length > TWENTY_FIVE) artistNameView.text =
                item.artistName!!.substring(0, TWENTY_FIVE) + ("...") else artistNameView.text =
                item.artistName
            if (item.trackName!!.length > TWENTY_FIVE) artistNameView.text =
                item.trackName!!.substring(0, TWENTY_FIVE) + ("...") else trackNameView.text =
                item.trackName
            trackTimeView.text = dateFormat.format(item.trackTimeMillis)

            Glide.with(itemView)
                .load(item.artworkUrl100)
                .centerCrop()
                .transform(RoundedCorners(dpToPx(itemView, 2f)))
                .placeholder(R.drawable.placeholder)
                .into(artworkUrlView)
        }

        private fun dpToPx(view: View, dp: Float): Int {
            val displayMetrics = view.resources.displayMetrics
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics)
                .toInt()
        }
    }
}



