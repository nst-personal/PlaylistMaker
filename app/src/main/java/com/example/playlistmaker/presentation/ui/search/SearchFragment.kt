package com.example.playlistmaker.presentation.ui.search

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.presentation.ui.media_player.MediaPlayerActivity
import com.example.playlistmaker.presentation.ui.search.interfaces.OnTrackItemClickListener
import com.example.playlistmaker.presentation.ui.search.interfaces.TrackScreenState
import com.example.playlistmaker.presentation.ui.search.view.adapter.TrackAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private var binding: FragmentSearchBinding? = null

    private var historyView: RecyclerView? = null
    private var recyclerView: RecyclerView? = null
    private var adapter: TrackAdapter? = null
    private var searchProgressBar: ProgressBar? = null

    private var searchValue: String = ""

    private var tracks = listOf<Track>()
    private var historyTracks = listOf<Track>()
    private val viewModel: SearchViewModel by viewModel()
    private var isItemClickAllowed = true
    private lateinit var textWatcher: TextWatcher

    private fun clickItemDebounce() : Boolean {
        val current = isItemClickAllowed
        if (isItemClickAllowed) {
            isItemClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(ITEM_BUTTON_DEBOUNCE_DELAY)
                isItemClickAllowed = true
            }
        }
        return current
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.showHistory()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            searchValue = savedInstanceState.getString(SEARCH, "")
        }
        binding?.inputEditText?.setText(searchValue)
        ViewCompat.setOnApplyWindowInsetsListener(binding?.search!!) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        searchProgressBar = binding?.searchProgressBarId

        val clearButton = binding?.clearIcon

        clearButton?.setOnClickListener {
            binding?.inputEditText?.setText("")
            handleHistoryView()
            viewModel.showHistory()
            searchProgressBar?.isVisible = false
        }

        recyclerView = binding?.tracksList
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        recyclerView?.isClickable = true

        historyView = binding?.historyTracksList
        historyView?.layoutManager = LinearLayoutManager(requireContext())
        historyView?.isClickable = true

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isVisible = !s.isNullOrEmpty()
                clearButton?.isVisible = isVisible
                if (isVisible) {
                    searchValue = s.toString()
                } else {
                    searchValue = ""
                }
                showHistory(false)
                showSearchNotFoundView(false)
                showSearchErrorView(false, "")
                recyclerView?.isVisible = false
                searchProgressBar?.isVisible = isVisible
                viewModel.searchDebounce(
                    changedText = s?.toString() ?: ""
                )
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }

        textWatcher?.let { binding?.inputEditText?.addTextChangedListener(it) }

        binding?.inputEditText?.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                handleHistoryView()
            }
        }

        val clearHistoryButton = binding?.clearHistory
        clearHistoryButton?.setOnClickListener {
            viewModel.clearHistory()
            showHistory(false)
            searchProgressBar?.isVisible = false
        }

        viewModel.getLoadingTrackLiveData().observe(viewLifecycleOwner) { screenState ->
            when (screenState) {
                is TrackScreenState.SearchContent -> {
                    if (screenState.tracks == null) {
                        showSearchErrorView(true, screenState.search)
                        showSearchNotFoundView(false)
                        showHistory(false)
                        recyclerView?.isVisible = false
                    } else {
                        handleTrackData(screenState.tracks, screenState.search)
                    }
                }
                is TrackScreenState.HistoryContent -> {
                    val trackClickListener = object : OnTrackItemClickListener {
                        override fun onItemClick(track: Track) {
                            openMediaPlayer(track)
                        }
                    }
                    historyTracks = screenState.tracks
                    historyView?.adapter = TrackAdapter(historyTracks, trackClickListener)
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        textWatcher?.let { binding?.inputEditText?.removeTextChangedListener(it) }
    }


    private fun openMediaPlayer(track: Track) {
        if (clickItemDebounce()) {
            viewModel.saveTrack(track)
            val displayMediaIntent = Intent(requireContext(), MediaPlayerActivity::class.java)
            startActivity(displayMediaIntent)
        }
    }

    private fun handleHistoryView() {
        recyclerView?.isVisible = false
        showHistory(historyTracks.isNotEmpty() && searchValue.isEmpty())
    }

    private fun handleSearchTracks(savedSearchValue: String) {
        if (savedSearchValue.isNotEmpty()) {
            viewModel.searchTracks(savedSearchValue)
        }
    }

    private fun handleTrackData(resultList: List<Track>?, savedSearchValue: String) {
        showSearchErrorView(false, savedSearchValue)
        if (resultList != null) {
            searchProgressBar?.isVisible = false
            if (resultList.isNotEmpty()) {
                tracks = resultList
                val trackClickListener = object : OnTrackItemClickListener {
                    override fun onItemClick(track: Track) {
                        viewModel.addTrack(track)
                        openMediaPlayer(track)
                    }
                }
                adapter = TrackAdapter(tracks, trackClickListener)
                recyclerView?.adapter = adapter
                recyclerView?.isVisible = true
                showSearchNotFoundView(false)
                showHistory(false)
            } else {
                showSearchNotFoundView(true)
                showHistory(false)
                recyclerView?.isVisible = false
            }
        } else {
            showSearchErrorView(true, savedSearchValue)
            showSearchNotFoundView(false)
            showHistory(false)
            recyclerView?.isVisible = false
        }
    }

    private fun showHistory(isVisible: Boolean) {
        if (isVisible) {
            viewModel.showHistory()
        }
        binding?.historyData?.isVisible = isVisible
    }

    private fun showSearchNotFoundView(isVisible: Boolean) {
        val searchNoDataTextView = binding?.searchNoDataText
        val searchNoDataImageView = binding?.searchNoDataIcon
        searchNoDataTextView?.isVisible = isVisible
        searchNoDataImageView?.isVisible = isVisible
        searchProgressBar?.isVisible = false
    }

    private fun showSearchErrorView(isVisible: Boolean, savedSearchValue: String) {
        val searchErrorTextView = binding?.searchErrorText
        val searchErrorConnectionTextView = binding?.searchErrorTextConnection
        val searchErrorImageView = binding?.searchErrorIcon
        val retryButton = binding?.retry
        searchErrorTextView?.isVisible = isVisible
        searchErrorTextView?.isVisible = isVisible
        searchErrorConnectionTextView?.isVisible = isVisible
        searchErrorImageView?.isVisible = isVisible
        retryButton?.isVisible = isVisible
        retryButton?.setOnClickListener{
            handleSearchTracks(savedSearchValue)
        }
        searchProgressBar?.isVisible = false
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH, searchValue)
    }

    private companion object {
        const val SEARCH = "SEARCH"
        const val ITEM_BUTTON_DEBOUNCE_DELAY = 1000L
    }
}
