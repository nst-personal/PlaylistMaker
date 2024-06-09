package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val backButton = findViewById<ImageView>(R.id.backId)
        backButton.setOnClickListener{
            finish()
        }
        val sendIntent = Intent(Intent.ACTION_SENDTO)
        val shareButton = findViewById<Button>(R.id.shareId)
        val shareButtonClickListener: View.OnClickListener = View.OnClickListener {
            sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.settings_shareadble_link))
            startActivity(Intent.createChooser(sendIntent, null))
        }
        shareButton.setOnClickListener(shareButtonClickListener)


        val supportButton = findViewById<Button>(R.id.supportId)
        val supportButtonClickListener: View.OnClickListener = View.OnClickListener {
            val shareIntent = Intent(Intent.ACTION_SENDTO)
            shareIntent.data = Uri.parse("mailto:")
            shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.settings_shareadble_email)))
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.settings_shareadble_text))
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.settings_shareadble_subject))
            startActivity(shareIntent)
        }
        supportButton.setOnClickListener(supportButtonClickListener)

        val tcButton = findViewById<Button>(R.id.tcId)
        val tcButtonClickListener: View.OnClickListener = View.OnClickListener {
            val shareIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.settings_shareadble_url)))
            startActivity(shareIntent)
        }
        tcButton.setOnClickListener(tcButtonClickListener)
    }
}