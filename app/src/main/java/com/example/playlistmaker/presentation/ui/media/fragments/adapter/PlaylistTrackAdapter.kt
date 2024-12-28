package com.example.playlistmaker.presentation.ui.media.fragments.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.ui.search.interfaces.OnTrackItemClickListener
import com.example.playlistmaker.presentation.ui.search.view.holder.TrackViewHolder

class PlaylistTrackAdapter(
    private val tracks: List<Track>,
    private val listener: OnTrackItemClickListener?
) : RecyclerView.Adapter<TrackViewHolder> () {

      override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_track_view_item, parent, false)
            return TrackViewHolder(view)
      }

      override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
            holder.bindView(tracks[position], listener)
      }

      override fun getItemCount() = tracks.size
}
