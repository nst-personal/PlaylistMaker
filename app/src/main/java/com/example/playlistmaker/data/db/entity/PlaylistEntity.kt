package com.example.playlistmaker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey
    val playlistId: Long,
    val playlistName: String,
    val playlistDescription: String?,
    val playlistImageUrl: String?,
    val playlistTracks: String,
    val playlistTracksCount: Long,
    val addedTime: Long? = null
)
