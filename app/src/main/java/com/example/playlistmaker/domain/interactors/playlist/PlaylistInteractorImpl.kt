package com.example.playlistmaker.domain.interactors.playlist

import com.example.playlistmaker.data.db.convertors.PlaylistDbConvertor
import com.example.playlistmaker.data.models.Playlist
import com.example.playlistmaker.data.models.PlaylistTrack
import com.example.playlistmaker.data.models.Track
import com.example.playlistmaker.domain.repositories.playlist.PlaylistRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository,
    private val gson: Gson,
    private val playlistDbConvertor: PlaylistDbConvertor
) : PlaylistInteractor {

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylists()
    }

    override fun getPlaylistIds(): Flow<List<Long>> {
        return playlistRepository.getPlaylistIds()
    }

    private fun maxValue(numbers: List<Long>):Long? {
        val maxNumber = if (numbers.isNotEmpty()) {
            numbers.maxOf { it }
        } else {
            null
        }
        return maxNumber
    }

    override suspend fun addPlaylist(playlistName: String,
                                     playlistDescription: String?,
                                     playlistUrl: String?) {
        getPlaylistIds().collect { playlistIds ->
            playlistDbConvertor.map(
                (maxValue(playlistIds)?.let { it + 1 } ?: 1).toLong(),
                playlistName,
                playlistDescription,
                playlistUrl
            )
        }

    }

    override suspend fun removePlaylist(playlist: Playlist) {
        playlistRepository.removePlaylist(playlist)
    }

    override suspend fun updatePlaylist(track: Track, playlist: Playlist): Boolean {
        var playlistTracks = PlaylistTrack(
            mutableListOf(),
        )
        if (playlist.playlistTracks.isNotEmpty()) {
            playlistTracks =
                gson.fromJson(playlist.playlistTracks, PlaylistTrack::class.java)
        }

        if (playlistTracks.tracks.filter { trackItem -> trackItem.trackId == track.trackId }
                .isEmpty()) {
            playlistTracks.tracks.add(track)
            playlist.playlistTracksCount = playlistTracks.tracks.size.toLong()
            playlist.playlistTracks = gson.toJson(playlistTracks)
            playlistRepository.updatePlaylist(playlist)
            return true
        }

        return false
    }
}