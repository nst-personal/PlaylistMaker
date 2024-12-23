package com.example.playlistmaker.presentation.ui.media.fragments

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.interactors.playlist.PlaylistInteractor
import com.example.playlistmaker.presentation.ui.media.fragments.interfaces.playlist.screen.PlaylistCreateScreenState
import kotlinx.coroutines.launch

class PlaylistCreateViewModel (
    private val playlistInteractor: PlaylistInteractor
): ViewModel() {

    private var loadingPlaylistLiveData = MutableLiveData<PlaylistCreateScreenState>()
    fun getLoadingPlaylistLiveData(): LiveData<PlaylistCreateScreenState> = loadingPlaylistLiveData

    fun savePlaylist(playlistName: String,
                     playlistDescription: String?,
                     playlistUrl: String?) {
        viewModelScope.launch {
            playlistInteractor.addPlaylist(playlistName, playlistDescription, playlistUrl)

        }
    }

    fun updateField(name: String, fieldName: String) {
        when (fieldName) {
            "name" -> {
                loadingPlaylistLiveData.postValue(
                    PlaylistCreateScreenState.PlaylistCreateCreateNameContent(
                        name
                    ))
            }
            "description" -> {
                loadingPlaylistLiveData.postValue(
                    PlaylistCreateScreenState.PlaylistCreateCreateDescriptionContent(
                        name
                    ))
            }
        }
    }

    fun updateURIField(uri: Uri, fieldName: String) {
        when (fieldName) {
            "photoUrl" -> {
                loadingPlaylistLiveData.postValue(
                    PlaylistCreateScreenState.PlaylistCreateCreatePhotoUrlContent(
                        uri
                    ))
            }
        }
    }

}