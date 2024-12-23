package com.example.playlistmaker.presentation.ui.media.fragments

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.data.models.Playlist
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.presentation.ui.media.fragments.adapter.PlaylistAdapter
import com.example.playlistmaker.presentation.ui.media.fragments.interfaces.playlist.PlaylistItem
import com.example.playlistmaker.presentation.ui.media.fragments.interfaces.playlist.screen.PlaylistListScreenState
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class PlayListFragment : Fragment() {

    companion object {
        fun newInstance() = PlayListFragment()
    }

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistListViewModel by viewModel()

    private var playlist: List<Playlist>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addNew.setOnClickListener({
            findNavController().navigate(R.id.action_media_to_playlistCreateFragment3)
        })
        viewModel.getLoadingPlaylistLiveData().observe(viewLifecycleOwner) { data ->
            handlePlaylistData(data)
        }
    }

    private fun handlePlaylistData(data: PlaylistListScreenState) {
        if (data is PlaylistListScreenState.PlaylistListContent) {
            playlist = data.playlists
            handlePlaylistView()
        }
    }

    private fun handlePlaylistView() {
        if (playlist?.isEmpty() == true) {
            binding.recyclerView.visibility = View.GONE
            binding.mediaNoDataText.visibility = View.VISIBLE
            binding.mediaNoDataIcon.visibility = View.VISIBLE
        } else {
            binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
            binding.recyclerView.adapter = PlaylistAdapter(
                playlist!!.map { item ->
                    var file: File? = null
                    if (item.playlistImageUrl?.isNotEmpty() == true) {
                        val filePath = File(
                            context?.getExternalFilesDir(
                                Environment.DIRECTORY_PICTURES
                            ), "myalbum"
                        )
                        file = File(filePath, item.playlistImageUrl?.substringAfterLast("/"))
                    }
                    PlaylistItem(item.playlistId, item.playlistName, item.playlistTracksCount, file)
                })
            binding.recyclerView.visibility = View.VISIBLE
            binding.mediaNoDataText.visibility = View.GONE
            binding.mediaNoDataIcon.visibility = View.GONE
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.showPlaylist()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}