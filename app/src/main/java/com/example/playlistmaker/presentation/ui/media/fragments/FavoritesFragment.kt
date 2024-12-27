package com.example.playlistmaker.presentation.ui.media.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.presentation.ui.media.fragments.interfaces.FavoriteTrackScreenState
import com.example.playlistmaker.presentation.ui.media_player.MediaPlayerActivity
import com.example.playlistmaker.presentation.ui.search.interfaces.OnTrackItemClickListener
import com.example.playlistmaker.presentation.ui.search.view.adapter.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    companion object {
        fun newInstance() = FavoritesFragment()
    }

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private var tracks = listOf<Track>()
    private val viewModel: FavoriteViewModel by viewModel()
    private var recyclerView: RecyclerView? = null
    private var adapter: TrackAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getLoadingTrackLiveData().observe(viewLifecycleOwner) { screenState ->
            if (screenState is FavoriteTrackScreenState.FavoriteContent) {
                if (screenState.tracks == null) {
                    showSearchNotFoundView(false)
                    recyclerView?.isVisible = false
                } else {
                    handleTrackData(screenState.tracks)
                }
            }
        }
        recyclerView = binding.tracksList
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        recyclerView?.isClickable = true
    }

    private fun openMediaPlayer(track: Track) {
        viewModel.saveTrack(track)
        val displayMediaIntent = Intent(requireContext(), MediaPlayerActivity::class.java)
        startActivity(displayMediaIntent)
    }

    override fun onResume() {
        super.onResume()
        viewModel.showFavorites()
    }

    private fun handleTrackData(resultList: List<Track>?) {
        if (resultList != null) {
            if (resultList.isNotEmpty()) {
                tracks = resultList
                val trackClickListener = object : OnTrackItemClickListener {
                    override fun onItemClick(track: Track) {
                        openMediaPlayer(track)
                    }
                }
                adapter = TrackAdapter(tracks, trackClickListener)
                recyclerView?.adapter = adapter
                recyclerView?.isVisible = true
                showSearchNotFoundView(false)
            } else {
                showSearchNotFoundView(true)
                recyclerView?.isVisible = false
            }
        } else {
            showSearchNotFoundView(false)
            recyclerView?.isVisible = false
        }
    }

    private fun showSearchNotFoundView(isVisible: Boolean) {
        binding.mediaNoDataIcon.isVisible = isVisible
        binding.mediaNoDataText.isVisible = isVisible
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}