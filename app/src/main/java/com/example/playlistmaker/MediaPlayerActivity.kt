package com.example.playlistmaker

import android.os.Bundle
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.configuration.ShareablePreferencesConfig
import com.example.playlistmaker.entities.Track
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_media_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mediaPlayer)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.tooltipId)
        setSupportActionBar(toolbar);

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener{
            finish()
        }
        fillContent()
    }

    private fun fillContent() {
        val media = getSharedPreferences(ShareablePreferencesConfig.CURRENT_MEDIA, MODE_PRIVATE)
        val track = Gson().fromJson(media.getString(ShareablePreferencesConfig.CURRENT_MEDIA, null), Track::class.java)
        setText(R.id.authorName, track.artistName)
        setText(R.id.trackName, track.trackName)
        setText(R.id.countryValue, track.country)
        setText(R.id.albumValue, track.collectionName)
        setText(R.id.typeValue, track.primaryGenreName)
        setText(R.id.yearValue, track.releaseDate)
        setText(R.id.time, SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime))
        setText(R.id.durationValue, SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime))

        val imageView = findViewById<com.google.android.material.imageview.ShapeableImageView>(R.id.imageTrack)
        Glide.with(imageView)
            .load(getCoverArtwork(track.artworkUrl100))
            .placeholder(R.drawable.placeholder)
            .into(imageView)
    }
    private fun setText(id: Int, text: String) {
        val view = findViewById<TextView>(id)
        view.setText(text)
    }

    fun getCoverArtwork(url: String) = url.replaceAfterLast('/',"512x512bb.jpg")

}