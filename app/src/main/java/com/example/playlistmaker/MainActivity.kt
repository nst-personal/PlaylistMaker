package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val displaySearchIntent = Intent(this, SearchActivity::class.java)
        val displaySettingsIntent = Intent(this, SettingsActivity::class.java)
        val displayMediaIntent = Intent(this, MediaActivity::class.java)

        val searchButton = findViewById<Button>(R.id.searchId)
        val searchButtonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                startActivity(displaySearchIntent)
            }
        }
        searchButton.setOnClickListener(searchButtonClickListener)

        val mediaButton = findViewById<Button>(R.id.mediaId)
        mediaButton.setOnClickListener{
            startActivity(displayMediaIntent)
        }

        val settingsButton = findViewById<Button>(R.id.settingsId)
        settingsButton.setOnClickListener{
            startActivity(displaySettingsIntent)
        }
    }
}