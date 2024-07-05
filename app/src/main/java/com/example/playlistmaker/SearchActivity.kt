package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.entities.Track
import com.example.playlistmaker.models.TrackResponse
import com.example.playlistmaker.services.TrackApi
import com.example.playlistmaker.view.adapter.TrackAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {
    lateinit var adapter: TrackAdapter
    private var searchValue: String = ""

    var tracks = listOf<Track>()

    private val translateBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(translateBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val trackService = retrofit.create(TrackApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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
        }

        val recyclerView = findViewById<RecyclerView>(R.id.tracksList)
        recyclerView.layoutManager = LinearLayoutManager(this)

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
                showSearchNotFoundView(false)
                showSearchErrorView(false, "", recyclerView)
                recyclerView.isVisible = true
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }

        inputEditText.addTextChangedListener(simpleTextWatcher)

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val savedSearchValue = searchValue
                handleSearchTracks(savedSearchValue, recyclerView)
                true
            }
            false
        }
    }

    private fun handleSearchTracks(savedSearchValue: String, recyclerView: RecyclerView) {
        trackService.search(searchValue)
            .enqueue(object : Callback<TrackResponse> {
                override fun onResponse(call: Call<TrackResponse>,
                                        response: Response<TrackResponse>
                ) {
                    handleTrackData(response, recyclerView, savedSearchValue)
                }
                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    showSearchErrorView(true, savedSearchValue, recyclerView)
                    showSearchNotFoundView(false)
                    recyclerView.isVisible = false
                }
            })
    }

    private fun handleTrackData(response: Response<TrackResponse>, recyclerView: RecyclerView, savedSearchValue: String) {
        showSearchErrorView(false, savedSearchValue, recyclerView)
        if (response.isSuccessful) {
            val resultList = response.body()?.results!!
            if (resultList.isNotEmpty()) {
                tracks = resultList
                adapter = TrackAdapter(tracks)
                recyclerView.adapter = adapter
                recyclerView.isVisible = true
                showSearchNotFoundView(false)
            } else {
                showSearchNotFoundView(true)
                recyclerView.isVisible = false
            }
        } else {
            showSearchErrorView(true, savedSearchValue, recyclerView)
            showSearchNotFoundView(false)
            recyclerView.isVisible = false
        }
    }

    private fun showSearchNotFoundView(isVisible: Boolean) {
        val searchNoDataTextView = findViewById<TextView>(R.id.searchNoDataText)
        val searchNoDataImageView = findViewById<ImageView>(R.id.searchNoDataIcon)
        searchNoDataTextView.isVisible = isVisible
        searchNoDataImageView.isVisible = isVisible
    }

    private fun showSearchErrorView(isVisible: Boolean, savedSearchValue: String, recyclerView: RecyclerView) {
        val searchErrorTextView = findViewById<TextView>(R.id.searchErrorText)
        val searchErrorConnectionTextView = findViewById<TextView>(R.id.searchErrorTextConnection)
        val searchErrorImageView = findViewById<ImageView>(R.id.searchErrorIcon)
        val retryButton = findViewById<Button>(R.id.retry)
        searchErrorTextView.isVisible = isVisible
        searchErrorConnectionTextView.isVisible = isVisible
        searchErrorImageView.isVisible = isVisible
        retryButton.isVisible = isVisible
        retryButton.setOnClickListener{
            handleSearchTracks(savedSearchValue, recyclerView)
        }
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

    private companion object {
        const val SEARCH = "SEARCH"
    }
}