package com.example.playlistmaker.search.ui

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

class TrackAdapter(
    private var arrayOfTrack: ArrayList<Track>, private var clickListener: RecyclerViewEvent
) : RecyclerView.Adapter<TrackAdapter.SearchViewHolder>() {
    fun addTracks(item: ArrayList<Track>) {
        arrayOfTrack.addAll(item)
        notifyItemRangeInserted(0, arrayOfTrack.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.track_item, parent, false,
        )
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(arrayOfTrack[position])
        holder.itemView.setOnClickListener {
            clickListener.onItemClick(arrayOfTrack[position])
        }
    }

    fun updateList(newList: ArrayList<Track>) {
        arrayOfTrack = newList
        notifyItemRangeRemoved(0, arrayOfTrack.size)
        notifyItemRangeInserted(0, arrayOfTrack.size)
    }

    override fun getItemCount(): Int {
        return arrayOfTrack.size
    }

    inner class SearchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val artistNameView: TextView = itemView.findViewById(R.id.artist_name)
        private val trackNameView: TextView = itemView.findViewById(R.id.track_name)
        private val trackTimeView: TextView = itemView.findViewById(R.id.track_time)
        private val artworkUrlView: ImageView = itemView.findViewById(R.id.track_image_url)
        private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
        fun bind(item: Track) {
            trackNameView.text = item.trackName
            artistNameView.text = item.artistName
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

interface RecyclerViewEvent {
    fun onItemClick(track: Track)
}


