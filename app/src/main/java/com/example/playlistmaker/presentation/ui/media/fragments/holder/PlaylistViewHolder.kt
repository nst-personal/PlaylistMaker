package com.example.playlistmaker.presentation.ui.media.fragments.holder

import android.os.Environment
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.ui.media.fragments.interfaces.playlist.PlaylistItem
import com.example.playlistmaker.utils.Utils.getLastPart
import java.io.File

class PlaylistViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val title: TextView = itemView.findViewById(R.id.title)
    private val size: TextView = itemView.findViewById(R.id.size)
    private val image: ImageView = itemView.findViewById(R.id.playlist_image)

    fun bind(item: PlaylistItem) {

        title.text = item.title
        if (item.trackCount.toInt() == 1) {
            size.text = item.trackCount.toString() + " " +
                    itemView.context.getString(R.string.playlist_track)
        } else {
            size.text = item.trackCount.toString() + " " +
                    itemView.context.getString(R.string.playlist_tracks)
        }
        if (item.url?.isNotEmpty() == true) {
            val filePath = File(itemView.context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), "myalbum"
            )
            val file = File(filePath, getLastPart(item.url))


            Glide.with(image)
                .load(file)
                .placeholder(R.drawable.placeholder)
                .into(image)

        } else {
            image.setImageResource(R.drawable.placeholder)
        }
    }
}
