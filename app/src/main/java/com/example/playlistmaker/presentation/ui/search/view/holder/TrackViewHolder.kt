package com.example.playlistmaker.presentation.ui.search.view.holder

import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.ui.search.interfaces.OnTrackItemClickListener
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackNameTextView: TextView = itemView.findViewById(R.id.trackName)
    private val artistNameTextView: TextView = itemView.findViewById(R.id.artistName)
    private val trackTimeTextView: TextView = itemView.findViewById(R.id.trackTime)
    private val imageView: ImageView = itemView.findViewById(R.id.image)

    fun bindView(track: Track, listener: OnTrackItemClickListener?) {
        itemView.setOnClickListener { listener?.onItemClick(track) }
        this.bind(track)
    }

    fun bind(model: Track) {
        trackNameTextView.text = model.trackName
        artistNameTextView.text = model.artistName
        trackTimeTextView.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis)
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