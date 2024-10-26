package com.example.playlistmaker.presentation.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private val viewModel: SettingsViewModel by viewModel()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        ViewCompat.setOnApplyWindowInsetsListener(binding.settings) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sendIntent = Intent(Intent.ACTION_SENDTO)
        val shareButton = binding.shareId
        val shareButtonClickListener: View.OnClickListener = View.OnClickListener {
            sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.settings_shareadble_link))
            startActivity(Intent.createChooser(sendIntent, null))
        }
        shareButton.setOnClickListener(shareButtonClickListener)

        val supportButton = binding.supportId
        val supportButtonClickListener: View.OnClickListener = View.OnClickListener {
            val shareIntent = Intent(Intent.ACTION_SENDTO)
            shareIntent.data = Uri.parse("mailto:")
            shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.settings_shareadble_email)))
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.settings_shareadble_text))
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.settings_shareadble_subject))
            startActivity(shareIntent)
        }
        supportButton.setOnClickListener(supportButtonClickListener)

        val tcButton = binding.tcId
        val tcButtonClickListener: View.OnClickListener = View.OnClickListener {
            val shareIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.settings_shareadble_url)))
            startActivity(shareIntent)
        }
        tcButton.setOnClickListener(tcButtonClickListener)

        val themeSwitcher = binding.themeSwitcher
        themeSwitcher.isChecked = (getActivity()?.getApplicationContext() as App).darkTheme

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (getActivity()?.getApplicationContext() as App).switchTheme(checked)
            viewModel.updateTheme(checked)
        }
        return binding.root
    }
}