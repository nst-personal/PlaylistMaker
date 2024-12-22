package com.example.playlistmaker.presentation.ui.search.view.holder

import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.data.models.Playlist
import com.example.playlistmaker.presentation.ui.media_player.interfaces.OnPlaylistItemClickListener
import com.example.playlistmaker.utils.Utils.getLastPart
import java.io.File

class PlaylistCreateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val playlistNameTextView: TextView = itemView.findViewById(R.id.playlistName)
    private val tracksCountTextView: TextView = itemView.findViewById(R.id.tracksCount)
    private val imageView: ImageView = itemView.findViewById(R.id.image)

    fun bindView(playlist: Playlist, listener: OnPlaylistItemClickListener?) {
        itemView.setOnClickListener { listener?.onItemClick(playlist) }
        this.bind(playlist)
    }

    fun bind(model: Playlist) {
        playlistNameTextView.text = model.playlistName
        if (model.playlistTracksCount.toInt() == 1) {
            tracksCountTextView.text = model.playlistTracksCount.toString() + " " +
                    itemView.context.getString(R.string.playlist_track)
        } else {
            tracksCountTextView.text = model.playlistTracksCount.toString() + " " +
                    itemView.context.getString(R.string.playlist_tracks)
        }

        if (model.playlistImageUrl?.isNotEmpty() == true) {
            val filePath = File(itemView.context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), "myalbum"
            )
            val file = File(filePath, getLastPart(model.playlistImageUrl))
            imageView.setImageURI(file.toUri())
        } else {
            imageView.setImageResource(R.drawable.placeholder)
        }
    }
}