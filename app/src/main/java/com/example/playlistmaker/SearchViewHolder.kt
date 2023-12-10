package com.example.playlistmaker

import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class SearchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val artistNameView: TextView = itemView.findViewById(R.id.artist_name)
    private val trackNameView: TextView = itemView.findViewById(R.id.track_name)
    private val trackTimeView: TextView = itemView.findViewById(R.id.track_time)
    private val artworkUrlView: ImageView = itemView.findViewById(R.id.track_image_url)

    fun bind(item: Track) {
        trackNameView.text = item.trackName
        artistNameView.text = item.artistName
        trackTimeView.text = item.trackTime
        Glide.with(itemView)
            .load(item.artworkUrlll)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(itemView, 2f)))
            .placeholder(R.drawable.placeholder)
            .into(artworkUrlView)
    }
}

private fun dpToPx(view: View, dp: Float): Int {
    val displayMetrics = view.resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics).toInt()
}