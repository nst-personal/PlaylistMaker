package com.example.playlistmaker.presentation.ui.media.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.net.toUri
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.presentation.ui.media.fragments.interfaces.playlist.screen.PlaylistCreateScreenState
import com.example.playlistmaker.presentation.ui.media.fragments.interfaces.playlist.screen.PlaylistEditScreenState
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistEditFragment : PlaylistCreateFragment() {

    override val viewModel: PlaylistEditViewModel by viewModel()
    private var playlistId: Long? = null
    private var editPlaylist: Playlist? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playlistId = arguments?.getLong("playlistId")
        viewModel.showPlaylist(playlistId!!)
        viewModel.getLoadingPlaylistLiveData().observe(viewLifecycleOwner) { data ->
            handlePlaylistData(data)
        }
        handleTitles()
    }

    private fun handleTitles() {
        binding.toolbarId.title = getResources().getString(R.string.media_edit_playlist)
        binding.btnSubmit.text = getResources().getString(R.string.save)
    }

    override fun handlePlaylistData(data: PlaylistCreateScreenState) {
        super.handlePlaylistData(data)
        if (data is PlaylistEditScreenState.PlaylistContent) {
            editPlaylist = data.playlist
            playlist?.playlistName = data.playlist.playlistName
            playlist?.playlistImageUrl = data.playlist.playlistImageUrl?.toUri()
            playlist?.playlistDescription = data.playlist.playlistDescription
            fillEditContent()
            validateContent()
        }
        if (data is PlaylistEditScreenState.PlaylistUpdateCompletedContent) {
            playlist = null
        }
    }

    override fun validateContent() {
        super.validateContent()
        this.isContentChanged =
            (editPlaylist?.playlistName?.equals(playlist?.playlistName) != null &&
                    editPlaylist?.playlistName?.contentEquals(playlist?.playlistName) != true
                    )
                    ||
                    (editPlaylist?.playlistDescription?.equals(playlist?.playlistDescription) != null &&
                            editPlaylist?.playlistDescription?.contentEquals(playlist?.playlistDescription) != true)
                    ||
                    (editPlaylist?.playlistImageUrl?.equals(playlist?.playlistImageUrl) != null &&
                            editPlaylist?.playlistImageUrl?.contentEquals(playlist?.playlistImageUrl?.toString()) != true)
    }

    private fun fillEditContent() {
        binding.tbName.setText(playlist?.playlistName)
        binding.tbDescription.setText(playlist?.playlistDescription)
        if (playlist?.playlistImageUrl != null) {
            binding.pickerImage.setImageURI(playlist?.playlistImageUrl)
            binding.pickerImagePreview.visibility = View.GONE
        }
    }

    override fun onPlaylistCreationClicked() {
        var path: String? = null
        var copyEditPlaylist = editPlaylist?.copy()

        if (playlist?.playlistImageUrl?.toString()?.isNotEmpty() == true &&
            editPlaylist?.playlistImageUrl?.contentEquals(playlist?.playlistImageUrl?.toString()) != true) {
            path = saveImageToStore()
            copyEditPlaylist?.playlistImageUrl = path
        }
        copyEditPlaylist?.playlistDescription = playlist?.playlistDescription
        copyEditPlaylist?.playlistName = playlist?.playlistName!!
        viewModel.updatePlaylist(
            copyEditPlaylist!!
        )

        Toast.makeText(
            requireContext(),
            "${getString(R.string.save_confirmation_playlist)} ${playlist?.playlistName} ${
                getString(
                    R.string.save_confirmation_updated
                )
            }", Toast.LENGTH_SHORT
        ).show()
        handleBack()
    }

}