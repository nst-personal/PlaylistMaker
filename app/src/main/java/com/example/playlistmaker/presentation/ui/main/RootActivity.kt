package com.example.playlistmaker.presentation.ui.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.ui.media.fragments.PlaylistCreateFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class RootActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            bottomNavigationView.isGone = destination.id == R.id.playlistCreateFragment3
        }

        if (savedInstanceState == null) {
            val fragmentId = intent.getStringExtra("fragmentId")
            val from = intent.getStringExtra("from")

            when (fragmentId) {
                "createPlaylist" -> {
                    val fragment = PlaylistCreateFragment().apply {
                        arguments = Bundle().apply {
                            putString("from", from)
                        }
                    }
                    bottomNavigationView.visibility = View.GONE
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.rootFragmentContainerView, fragment)
                        .commit()
                }
            }
        }
    }

}