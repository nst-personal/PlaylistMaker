package com.example.playlistmaker.presentation.ui.search.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.ui.media.fragments.interfaces.playlist.PlaylistItem
import com.example.playlistmaker.presentation.ui.media_player.interfaces.OnPlaylistItemClickListener
import com.example.playlistmaker.presentation.ui.search.view.holder.PlaylistCreateViewHolder

class PlaylistCreateAdapter(
    private val playlist: List<PlaylistItem>,
    private val listener: OnPlaylistItemClickListener?
) : RecyclerView.Adapter<PlaylistCreateViewHolder> () {

      override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistCreateViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_create_view_item, parent, false)
            return PlaylistCreateViewHolder(view)
      }

      override fun onBindViewHolder(holder: PlaylistCreateViewHolder, position: Int) {
            holder.bindView(playlist[position], listener)
      }

      override fun getItemCount() = playlist.size
}
