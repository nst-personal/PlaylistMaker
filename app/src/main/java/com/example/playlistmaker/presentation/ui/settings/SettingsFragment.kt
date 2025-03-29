package com.example.playlistmaker.presentation.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!


    private val viewModel: SettingsViewModel by viewModel()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSettingsBinding.inflate(layoutInflater)
        ViewCompat.setOnApplyWindowInsetsListener(binding.settings) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sendIntent = Intent(Intent.ACTION_SENDTO)
//        val shareButton = binding.shareId
//        val shareButtonClickListener: View.OnClickListener = View.OnClickListener {
//            sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.settings_shareadble_link))
//            startActivity(Intent.createChooser(sendIntent, null))
//        }
//        shareButton.setOnClickListener(shareButtonClickListener)
//
//        val supportButton = binding.supportId
//        val supportButtonClickListener: View.OnClickListener = View.OnClickListener {
//            val shareIntent = Intent(Intent.ACTION_SENDTO)
//            shareIntent.data = Uri.parse("mailto:")
//            shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.settings_shareadble_email)))
//            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.settings_shareadble_text))
//            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.settings_shareadble_subject))
//            startActivity(shareIntent)
//        }
//        supportButton.setOnClickListener(supportButtonClickListener)
//
//        val tcButton = binding.tcId
//        val tcButtonClickListener: View.OnClickListener = View.OnClickListener {
//            val shareIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.settings_shareadble_url)))
//            startActivity(shareIntent)
//        }
//        tcButton.setOnClickListener(tcButtonClickListener)
//
//        val themeSwitcher = binding.themeSwitcher
//        themeSwitcher.isChecked = (requireActivity().getApplicationContext() as App).darkTheme
         val darkTheme = (requireActivity().getApplicationContext() as App).darkTheme
//
//        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
//            (getActivity()?.getApplicationContext() as App).switchTheme(checked)
//            viewModel.updateTheme(checked)
//        }

        binding.composeView.setContent {
            SettingsScreen(viewModel = viewModel, darkTheme = darkTheme)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


@Composable
fun SettingsScreen(viewModel: SettingsViewModel, darkTheme: Boolean) {

    val themeSwitcherState = remember { mutableStateOf(darkTheme) }
    val textColor = if (isSystemInDarkTheme()) {
        colorResource(id = R.color.white)
    } else {
        colorResource(id = R.color.black)
    }
    themeSwitcherState.value = isSystemInDarkTheme()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 8.dp, end = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.settings_title),
                style = MaterialTheme.typography.h6,
                color = textColor,
                fontSize = 18.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.settings_dark_mode),
                style = MaterialTheme.typography.body1,
                color = textColor,
                fontSize = 18.sp,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            )

            Switch(
                checked = themeSwitcherState.value,
                onCheckedChange = { checked ->
                    viewModel.updateTheme(checked)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = colorResource(id = R.color.blue),
                    uncheckedThumbColor = colorResource(id = R.color.textbox),
                ),
                modifier = Modifier.padding(end = 8.dp)
            )
        }

        SettingsItemButton(
            text = stringResource(id = R.string.settings_share_app),
            icon = R.drawable.share,
            onClick = {

            }
        )
        
        SettingsItemButton(
            text = stringResource(id = R.string.settings_support),
            icon = R.drawable.support,
            onClick = {

            }
        )
        
        SettingsItemButton(
            text = stringResource(id = R.string.settings_tc),
            icon = R.drawable.tc,
            onClick = {

            }
        )
    }
}

@Composable
private fun SettingsItemButton(
    text: String,
    icon: Int,
    onClick: () -> Unit
) {
    val textColor = if (isSystemInDarkTheme()) {
        colorResource(id = R.color.white)
    } else {
        colorResource(id = R.color.black)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.body1,
            color = textColor,
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        )
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .padding(end = 16.dp)
        )
    }
}