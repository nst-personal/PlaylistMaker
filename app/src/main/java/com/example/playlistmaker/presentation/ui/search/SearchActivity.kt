package com.example.playlistmaker.presentation.ui.search

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.data.models.Track
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.presentation.ui.media_player.MediaPlayerActivity
import com.example.playlistmaker.presentation.ui.search.interfaces.OnTrackItemClickListener
import com.example.playlistmaker.presentation.ui.search.interfaces.TrackScreenState
import com.example.playlistmaker.presentation.ui.search.view.adapter.TrackAdapter


class SearchActivity : AppCompatActivity() {
    private lateinit var historyView: RecyclerView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TrackAdapter
    private lateinit var searchProgressBar: ProgressBar

    private lateinit var viewModel: SearchViewModel

    private var searchValue: String = ""

    private var tracks = listOf<Track>()
    private lateinit var binding: ActivitySearchBinding
    private val itemClickHandler = Handler(Looper.getMainLooper())

    private var isItemClickAllowed = true

    private fun clickItemDebounce() : Boolean {
        val current = isItemClickAllowed
        if (isItemClickAllowed) {
            isItemClickAllowed = false
            itemClickHandler.postDelayed({ isItemClickAllowed = true }, ITEM_BUTTON_DEBOUNCE_DELAY)
        }
        return current
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        viewModel = ViewModelProvider(
            this, SearchViewModel.getViewModelFactory(this)
        )[SearchViewModel::class.java]

        searchProgressBar = binding.searchProgressBarId

        val backButton = binding.backId
        backButton.setOnClickListener{
            finish()
        }
        val clearButton = binding.clearIcon

        clearButton.setOnClickListener {
            binding.inputEditText.setText("")
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.main.windowToken, 0)
            handleHistoryView()
            searchProgressBar.isVisible = false
        }

        recyclerView = binding.tracksList
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.isClickable = true

        historyView = binding.historyTracksList
        historyView.layoutManager = LinearLayoutManager(this)
        historyView.isClickable = true

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isVisible = !s.isNullOrEmpty()
                clearButton.isVisible = isVisible
                if (isVisible) {
                    searchValue = s.toString()
                } else {
                    searchValue = ""
                }
                showHistory(false)
                showSearchNotFoundView(false)
                showSearchErrorView(false, "")
                recyclerView.isVisible = false
                searchProgressBar.isVisible = true
                viewModel.searchDebounce(
                    changedText = s?.toString() ?: ""
                )
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }

        binding.inputEditText.addTextChangedListener(simpleTextWatcher)

        binding.inputEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                handleHistoryView()
            }
        }

        val clearHistoryButton = binding.clearHistory
        clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
            showHistory(false)
            searchProgressBar.isVisible = false
        }

        viewModel.getLoadingTrackLiveData().observe(this) { screenState ->
            when (screenState) {
                is TrackScreenState.SearchContent -> {
                    if (screenState.tracks == null) {
                        showSearchErrorView(true, screenState.search)
                        showSearchNotFoundView(false)
                        showHistory(false)
                        recyclerView.isVisible = false
                    } else {
                        handleTrackData(screenState.tracks, screenState.search)
                    }
                }
            }
        }

    }

    private fun openMediaPlayer(track: Track) {
        if (clickItemDebounce()) {
            viewModel.saveTrack(track)
            val displayMediaIntent = Intent(this, MediaPlayerActivity::class.java)
            startActivity(displayMediaIntent)
        }
    }

    private fun handleHistoryView() {
        recyclerView.isVisible = false
        showHistory(!viewModel.isHistoryVisible() && searchValue.isEmpty())
    }

    private fun handleSearchTracks(savedSearchValue: String) {
        viewModel.searchTracks(savedSearchValue);
    }

    private fun handleTrackData(resultList: List<Track>?, savedSearchValue: String) {
        showSearchErrorView(false, savedSearchValue)
        if (resultList != null) {
            searchProgressBar.isVisible = false
            if (resultList.isNotEmpty()) {
                tracks = resultList
                val trackClickListener = object : OnTrackItemClickListener {
                    override fun onItemClick(track: Track) {
                        viewModel.addTrack(track)
                        openMediaPlayer(track)
                    }
                }
                adapter = TrackAdapter(tracks, trackClickListener)
                recyclerView.adapter = adapter
                recyclerView.isVisible = true
                showSearchNotFoundView(false)
                showHistory(false)
            } else {
                showSearchNotFoundView(true)
                showHistory(false)
                recyclerView.isVisible = false
            }
        } else {
            showSearchErrorView(true, savedSearchValue)
            showSearchNotFoundView(false)
            showHistory(false)
            recyclerView.isVisible = false
        }
    }

    private fun showHistory(isVisible: Boolean) {
        if (isVisible) {
            val trackClickListener = object : OnTrackItemClickListener {
                override fun onItemClick(track: Track) {
                    openMediaPlayer(track)
                }
            }
            historyView.adapter = TrackAdapter(viewModel.getHistory(), trackClickListener)
        }
        val searchNoDataTextView = binding.historyData
        searchNoDataTextView.isVisible = isVisible
    }

    private fun showSearchNotFoundView(isVisible: Boolean) {
        val searchNoDataTextView = binding.searchNoDataText
        val searchNoDataImageView = binding.searchNoDataIcon
        searchNoDataTextView.isVisible = isVisible
        searchNoDataImageView.isVisible = isVisible
        searchProgressBar.isVisible = false
    }

    private fun showSearchErrorView(isVisible: Boolean, savedSearchValue: String) {
        val searchErrorTextView = binding.searchErrorText
        val searchErrorConnectionTextView = binding.searchErrorTextConnection
        val searchErrorImageView = binding.searchErrorIcon
        val retryButton = binding.retry
        searchErrorTextView.isVisible = isVisible
        searchErrorTextView.isVisible = isVisible
        searchErrorConnectionTextView.isVisible = isVisible
        searchErrorImageView.isVisible = isVisible
        retryButton.isVisible = isVisible
        retryButton.setOnClickListener{
            handleSearchTracks(savedSearchValue)
        }
        searchProgressBar.isVisible = false
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH, searchValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchValue = savedInstanceState.getString(SEARCH, "")
        binding.inputEditText.setText(searchValue)
    }

    private companion object {
        const val SEARCH = "SEARCH"
        const val ITEM_BUTTON_DEBOUNCE_DELAY = 1000L
    }
}