package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.configuration.ShareablePreferencesConfig
import com.example.playlistmaker.entities.Track
import com.example.playlistmaker.interfaces.OnTrackItemClickListener
import com.example.playlistmaker.models.TrackResponse
import com.example.playlistmaker.services.SearchHistory
import com.example.playlistmaker.services.TrackApi
import com.example.playlistmaker.view.adapter.TrackAdapter
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {
    lateinit var adapter: TrackAdapter
    private lateinit var historyService: SearchHistory
    private lateinit var historyView: RecyclerView
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchProgressBar: ProgressBar

    private var searchValue: String = ""

    private var tracks = listOf<Track>()

    private val translateBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(translateBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val trackService = retrofit.create(TrackApi::class.java)

    private val searchHandler = Handler(Looper.getMainLooper())
    private val itemClickHandler = Handler(Looper.getMainLooper())

    private val searchRunnable = Runnable { handleSearchTracks(searchValue) }

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
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        searchProgressBar = findViewById(R.id.searchProgressBarId)

        historyService = SearchHistory(getSharedPreferences(ShareablePreferencesConfig.HISTORY_LIST, MODE_PRIVATE))

        val backButton = findViewById<ImageView>(R.id.backId)
        backButton.setOnClickListener{
            finish()
        }
        val mainLayout = findViewById<LinearLayout>(R.id.main)
        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)

        clearButton.setOnClickListener {
            inputEditText.setText("")
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(mainLayout.windowToken, 0)
            handleHistoryView()
            searchProgressBar.isVisible = false
        }

        recyclerView = findViewById(R.id.tracksList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.isClickable = true

        historyView = findViewById(R.id.historyTracksList)
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
                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }

        inputEditText.addTextChangedListener(simpleTextWatcher)

        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                handleHistoryView()
            }
        }

        val clearHistoryButton = findViewById<Button>(R.id.clearHistory)
        clearHistoryButton.setOnClickListener {
            historyService.remove()
            showHistory(false)
            searchProgressBar.isVisible = false
        }

    }

    private fun openMediaPlayer(track: Track) {
        if (clickItemDebounce()) {
            val sharedPreferences = getSharedPreferences(ShareablePreferencesConfig.CURRENT_MEDIA, MODE_PRIVATE)
            sharedPreferences.edit().putString(ShareablePreferencesConfig.CURRENT_MEDIA, Gson().toJson(track)).apply()
            val displayMediaIntent = Intent(this, MediaPlayerActivity::class.java)
            startActivity(displayMediaIntent)
        }
    }

    private fun handleHistoryView() {
        recyclerView.isVisible = false
        showHistory(!historyService.isEmpty() && searchValue.isEmpty())
    }

    private fun handleSearchTracks(savedSearchValue: String) {
        trackService.search(searchValue)
            .enqueue(object : Callback<TrackResponse> {
                override fun onResponse(call: Call<TrackResponse>,
                                        response: Response<TrackResponse>
                ) {
                    handleTrackData(response, savedSearchValue)
                }
                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    showSearchErrorView(true, savedSearchValue)
                    showSearchNotFoundView(false)
                    showHistory(false)
                    recyclerView.isVisible = false
                }
            })
    }

    private fun handleTrackData(response: Response<TrackResponse>, savedSearchValue: String) {
        showSearchErrorView(false, savedSearchValue)
        if (response.isSuccessful) {
            searchProgressBar.isVisible = false
            val resultList = response.body()?.results!!
            if (resultList.isNotEmpty()) {
                tracks = resultList
                val trackClickListener = object : OnTrackItemClickListener {
                    override fun onItemClick(track: Track) {
                        historyService.add(track)
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
            historyView.adapter = TrackAdapter(historyService.findAll(), trackClickListener)
        }
        val searchNoDataTextView = findViewById<LinearLayout>(R.id.historyData)
        searchNoDataTextView.isVisible = isVisible
    }

    private fun showSearchNotFoundView(isVisible: Boolean) {
        val searchNoDataTextView = findViewById<TextView>(R.id.searchNoDataText)
        val searchNoDataImageView = findViewById<ImageView>(R.id.searchNoDataIcon)
        searchNoDataTextView.isVisible = isVisible
        searchNoDataImageView.isVisible = isVisible
        searchProgressBar.isVisible = false
    }

    private fun showSearchErrorView(isVisible: Boolean, savedSearchValue: String) {
        val searchErrorTextView = findViewById<TextView>(R.id.searchErrorText)
        val searchErrorConnectionTextView = findViewById<TextView>(R.id.searchErrorTextConnection)
        val searchErrorImageView = findViewById<ImageView>(R.id.searchErrorIcon)
        val retryButton = findViewById<Button>(R.id.retry)
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
        findViewById<EditText>(R.id.inputEditText).setText(searchValue)
    }

    private fun searchDebounce() {
        searchHandler.removeCallbacks(searchRunnable)
        searchHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private companion object {
        const val SEARCH = "SEARCH"
        const val SEARCH_DEBOUNCE_DELAY = 2000L
        const val ITEM_BUTTON_DEBOUNCE_DELAY = 1000L
    }
}