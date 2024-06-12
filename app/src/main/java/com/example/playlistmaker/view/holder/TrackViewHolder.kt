package com.example.playlistmaker.view.holder

import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.entities.Track

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackNameTextView: TextView = itemView.findViewById(R.id.trackName)
    private val artistNameTextView: TextView = itemView.findViewById(R.id.artistName)
    private val trackTimeTextView: TextView = itemView.findViewById(R.id.trackTime)
    private val imageView: ImageView = itemView.findViewById(R.id.image)

    fun bind(model: Track) {
        trackNameTextView.text = model.trackName
        artistNameTextView.text = model.artistName
        trackTimeTextView.text = model.trackTime
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    4F,
                    itemView.resources.displayMetrics).toInt()
            ))
            .into(imageView)
    }
}