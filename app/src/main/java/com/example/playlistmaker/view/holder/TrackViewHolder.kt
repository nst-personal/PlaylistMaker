package com.example.playlistmaker.view.holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.entities.Track

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackNameTextView: TextView
    private val artistNameTextView: TextView
    private val trackTimeTextView: TextView
    private val imageView: ImageView

    init {
        trackNameTextView = itemView.findViewById(R.id.trackName)
        artistNameTextView = itemView.findViewById(R.id.artistName)
        trackTimeTextView = itemView.findViewById(R.id.trackTime)
        imageView = itemView.findViewById(R.id.image)
    }

    fun bind(model: Track) {
        trackNameTextView.text = model.trackName
        artistNameTextView.text = model.artistName
        trackTimeTextView.text = model.trackTime
        Glide.with(itemView).load(model.artworkUrl100).placeholder(R.drawable.placeholder).into(imageView)
    }
}