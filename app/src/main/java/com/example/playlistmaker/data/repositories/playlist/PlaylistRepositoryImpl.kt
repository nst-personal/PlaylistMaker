package com.example.playlistmaker.data.repositories.playlist

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.convertors.PlaylistDbConvertor
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.models.Playlist
import com.example.playlistmaker.domain.repositories.playlist.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConvertor: PlaylistDbConvertor,
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


    override suspend fun addPlaylist(playlist: Playlist) {
        appDatabase.playlistDao().insertPlaylist(
            playlistDbConvertor.map(playlist)
        )
    }

    override suspend fun removePlaylist(playlist: Playlist) {
        appDatabase.playlistDao().deletePlaylist(
            playlistDbConvertor.map(playlist)
        )
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        appDatabase.playlistDao().updatePlaylist(
            playlistDbConvertor.map(playlist)
        )
    }

    private fun convertFromPlaylistEntity(movies: List<PlaylistEntity>): List<Playlist> {
        return movies.map { playlist -> playlistDbConvertor.map(playlist) }.sortedByDescending { playlist -> playlist.addedTime }
    }
}