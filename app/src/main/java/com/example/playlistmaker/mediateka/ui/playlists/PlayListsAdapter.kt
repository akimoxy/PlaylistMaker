package com.example.playlistmaker.mediateka.ui.playlists

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.mediateka.domain.model.PlaylistsModel

class PlayListsAdapter(private var playlistList: ArrayList<PlaylistsModel>, val onPlaylistClick: OnPlaylistClick) :
    RecyclerView.Adapter<PlayListsAdapter.PlaylistsViewHolder>() {

  
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.playlist_item, parent, false)
        return PlaylistsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return playlistList.size
    }

    override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
        holder.bind(playlistList[position])
        holder.itemView.setOnClickListener {
            onPlaylistClick.onItemClick(playlistList[position])
        }
    }

    fun updateList(newList: ArrayList<PlaylistsModel>) {
        var list: ArrayList<PlaylistsModel> = arrayListOf()
        list.clear()
        list.addAll(newList)
        playlistList.clear()
        playlistList.addAll(list)
        //   notifyItemRangeRemoved(0, itemCount)
        //   notifyItemRangeInserted(0, newList.size)
        this.notifyDataSetChanged()

    }

    class PlaylistsViewHolder(view: View) : RecyclerView.ViewHolder(view) {



        private val playlistListName: TextView = itemView.findViewById(R.id.text_view_playlist_name)
        private val countOfTracks: TextView = itemView.findViewById(R.id.text_view_count_tracks)
        private val imageView: ImageView = itemView.findViewById(R.id.playlist_image)


        fun bind(playList: PlaylistsModel) {
            playlistListName.text = playList.playlistName
            countOfTracks.text =  playList.countOfTracksWithText

Log.d("холдер", playList.countOfTracksWithText)


            Glide.with(itemView)
                .load(playList.imageStorageLink)
                .placeholder((R.drawable.placeholder))
                .into(imageView)
        }

        fun Context.quantityFromRes(id_: Int, qtt: Int, vararg format: Any): String? {
            return resources.getQuantityString(id_, qtt, *format)
        }


    }
}
interface OnPlaylistClick {
    fun onItemClick(playList: PlaylistsModel)
}
