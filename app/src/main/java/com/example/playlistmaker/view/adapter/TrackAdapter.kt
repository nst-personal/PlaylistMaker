package com.example.playlistmaker.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.entities.Track
import com.example.playlistmaker.view.holder.TrackViewHolder

class TrackAdapter(
      private val tracks: List<Track>
) : RecyclerView.Adapter<TrackViewHolder> () {

      override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view_item, parent, false)
            return TrackViewHolder(view)
     }

      override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
            holder.bind(tracks[position])
      }

      override fun getItemCount() = tracks.size
}
