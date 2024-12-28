package com.example.playlistmaker.presentation.ui.media.fragments.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.ui.media.fragments.holder.PlaylistTrackViewHolder
import com.example.playlistmaker.presentation.ui.media_player.interfaces.OnPlaylistTrackItemClickListener
import com.example.playlistmaker.presentation.ui.search.interfaces.OnTrackItemClickListener
import com.example.playlistmaker.presentation.ui.search.view.holder.TrackViewHolder

class PlaylistTrackAdapter(
    private val tracks: List<Track>,
    private val listener: OnPlaylistTrackItemClickListener?
) : RecyclerView.Adapter<PlaylistTrackViewHolder> () {

      override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistTrackViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_track_view_item, parent, false)
            return PlaylistTrackViewHolder(view)
      }

      override fun onBindViewHolder(holder: PlaylistTrackViewHolder, position: Int) {
            holder.bindView(tracks[position], listener)
      }

      override fun getItemCount() = tracks.size
}
