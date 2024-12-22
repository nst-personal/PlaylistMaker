package com.example.playlistmaker.data.db.convertors

import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.models.Playlist
import java.time.Instant

class PlaylistDbConvertor {
    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            playlist.playlistId,
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.playlistImageUrl,
            playlist.playlistTracks,
            playlist.playlistTracksCount,
            playlist.addedTime,
        )
    }

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.playlistId,
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.playlistImageUrl,
            playlist.playlistTracks,
            playlist.playlistTracksCount,
            playlist.addedTime,
        )
    }

    fun map(
        id: Long,
        name: String,
        description: String?,
        url: String?
    ): Playlist {
        return Playlist(
            id,
            name,
            description,
            url,
            "",
            0,
            Instant.now().toEpochMilli(),
        )
    }
}
