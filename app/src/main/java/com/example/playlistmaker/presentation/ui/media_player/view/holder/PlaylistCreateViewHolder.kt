package com.example.playlistmaker.presentation.ui.search.view.holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.ui.media.fragments.interfaces.playlist.PlaylistItem
import com.example.playlistmaker.presentation.ui.media_player.interfaces.OnPlaylistItemClickListener

class PlaylistCreateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val playlistNameTextView: TextView = itemView.findViewById(R.id.playlistName)
    private val tracksCountTextView: TextView = itemView.findViewById(R.id.tracksCount)
    private val imageView: ImageView = itemView.findViewById(R.id.image)

    fun bindView(playlist: PlaylistItem, listener: OnPlaylistItemClickListener?) {
        itemView.setOnClickListener { listener?.onItemClick(playlist) }
        this.bind(playlist)
    }

    fun bind(model: PlaylistItem) {
        playlistNameTextView.text = model.title
        if (model.trackCount.toInt() == 1) {
            tracksCountTextView.text = buildString {
                append(model.trackCount.toString())
                append(" ")
                append(itemView.context.getString(R.string.playlist_track))
            }
        } else {
            tracksCountTextView.text = buildString {
                append(model.trackCount.toString())
                append(" ")
                append(itemView.context.getString(R.string.playlist_tracks))
            }
        }
        if (model.file != null) {
            Glide.with(imageView)
                .load(model.file)
                .placeholder(R.drawable.placeholder)
                .into(imageView)
        } else {
            imageView.setImageResource(R.drawable.placeholder)
        }
    }
}