package com.example.playlistmaker.data.repositories.playlist

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.convertors.PlaylistDbConvertor
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.dto.playlist.PlaylistTrackDto
import com.example.playlistmaker.data.dto.search.TrackDto
import com.example.playlistmaker.data.models.Playlist
import com.example.playlistmaker.data.models.Track
import com.example.playlistmaker.domain.repositories.playlist.PlaylistRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val gson: Gson,
    private val playlistDbConvertor: PlaylistDbConvertor
) : PlaylistRepository {

    override fun getPlaylists(): Flow<List<Playlist>> {
        return flow {
            val playlists = appDatabase.playlistDao().getPlaylist()
            emit(convertFromPlaylistEntity(playlists))
        }
    }

    override fun getPlaylistIds(): Flow<List<Long>> {
        return flow {
            val playlistIds = appDatabase.playlistDao().getPlaylistIds()
            emit(playlistIds)
        }
    }

    private fun maxValue(numbers: List<Long>): Long? {
        val maxNumber = if (numbers.isNotEmpty()) {
            numbers.maxOf { it }
        } else {
            null
        }
        return maxNumber
    }

    override suspend fun addPlaylist(
        playlistName: String,
        playlistDescription: String?,
        playlistUrl: String?
    ) {
        getPlaylistIds().collect { playlistIds ->
            appDatabase.playlistDao().insertPlaylist(
                playlistDbConvertor.map(
                    playlistDbConvertor.map(
                        (maxValue(playlistIds)?.let { it + 1 } ?: 1).toLong(),
                        playlistName,
                        playlistDescription,
                        playlistUrl
                    )
                )
            )
        }
    }

    override suspend fun removePlaylist(playlist: Playlist) {
        appDatabase.playlistDao().deletePlaylist(
            playlistDbConvertor.map(playlist)
        )
    }

    override suspend fun updatePlaylist(track: Track, playlist: Playlist): Boolean {
        var playlistTracks = PlaylistTrackDto(
            mutableListOf(),
        )
        if (playlist.playlistTracks.isNotEmpty()) {
            playlistTracks =
                gson.fromJson(playlist.playlistTracks, PlaylistTrackDto::class.java)
        }

        if (playlistTracks.tracks.filter { trackItem -> trackItem.trackId == track.trackId }
                .isEmpty()) {
            playlistTracks.tracks.add(
                TrackDto(
                    track.trackName,
                    track.trackId,
                    track.artistName,
                    track.trackTimeMillis,
                    track.artworkUrl100,
                    track.previewUrl,
                    track.collectionName,
                    track.releaseDate,
                    track.primaryGenreName,
                    track.country
                )
            )
            playlist.playlistTracksCount = playlistTracks.tracks.size.toLong()
            playlist.playlistTracks = gson.toJson(playlistTracks)
            appDatabase.playlistDao().updatePlaylist(
                playlistDbConvertor.map(playlist)
            )
            return true
        }

        return false
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConvertor.map(playlist) }
            .sortedByDescending { playlist -> playlist.addedTime }
    }
}