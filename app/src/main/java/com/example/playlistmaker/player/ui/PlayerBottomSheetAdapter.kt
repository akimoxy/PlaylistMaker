package com.example.playlistmaker.player.ui

import android.annotation.SuppressLint
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
import com.example.playlistmaker.mediateka.domain.models.PlaylistsModel

class PlayerBottomSheetAdapter(
    private var arrayOfPlaylists: ArrayList<PlaylistsModel>, val rv:RVEvent
) : RecyclerView.Adapter<PlayerBottomSheetAdapter.BottomSheetPlayerViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BottomSheetPlayerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.btm_sheet_playlists_item, parent, false,
        )
        return BottomSheetPlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BottomSheetPlayerViewHolder, position: Int) {
        holder.bind(arrayOfPlaylists[position])
        holder.itemView.setOnClickListener {
            rv.onItemClick(arrayOfPlaylists[position])
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: ArrayList<PlaylistsModel>) {
        val list: ArrayList<PlaylistsModel> = arrayListOf()
        list.clear()
        list.addAll(newList)
        arrayOfPlaylists.clear()
        arrayOfPlaylists.addAll(list)
        //   notifyItemRangeRemoved(0, itemCount)
        //   notifyItemRangeInserted(0, newList.size)
        this.notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return arrayOfPlaylists.size
    }

    inner class BottomSheetPlayerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val playlistListName: TextView = itemView.findViewById(R.id.tv_playlist_name)
        private val countOfTracks: TextView = itemView.findViewById(R.id.tv_count_of_tracks)
        private val imageView: ImageView = itemView.findViewById(R.id.track_image_url)

        @SuppressLint("SetTextI18n")
        fun bind(playList: PlaylistsModel) {
            playlistListName.text = playList.playlistName
            countOfTracks.text = playList.countOfTracks.toString()
            Glide.with(itemView)
                .load(playList.imageStorageLink)
                .centerCrop()
                .transform(RoundedCorners(dpToPx(itemView, 2f)))
                .placeholder((R.drawable.placeholder))
                .into(imageView)
        }
    }
    private fun dpToPx(view: View, dp: Float): Int {
        val displayMetrics = view.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics)
            .toInt()
    }
}
interface RVEvent {
    fun onItemClick(playlist: PlaylistsModel)
}
