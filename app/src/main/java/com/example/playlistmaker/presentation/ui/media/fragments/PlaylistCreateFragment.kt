package com.example.playlistmaker.presentation.ui.media.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistCreateBinding
import com.example.playlistmaker.presentation.ui.media.fragments.interfaces.playlist.screen.PlaylistCreate
import com.example.playlistmaker.presentation.ui.media.fragments.interfaces.playlist.screen.PlaylistCreateScreenState
import com.example.playlistmaker.presentation.ui.media_player.interfaces.OnFragmentRemovedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class PlaylistCreateFragment : Fragment() {
    private var listener: OnFragmentRemovedListener? = null

    private var _binding: FragmentPlaylistCreateBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistCreateViewModel by viewModel()

    private var playlist: PlaylistCreate? = null
    private var isContentChanged: Boolean = false

    private lateinit var confirmDialog: MaterialAlertDialogBuilder
    private lateinit var textWatcherName: TextWatcher
    private lateinit var textWatcherDescription: TextWatcher

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentRemovedListener) {
            listener = context
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null) {
            binding?.tbName?.setText(savedInstanceState.getString(NAME, ""))
            binding?.tbDescription?.setText(savedInstanceState.getString(DESCRIPTION, ""))
            if (savedInstanceState.getString(URI, "")?.isNotEmpty() == true) {
                binding?.pickerImage?.setImageURI(savedInstanceState.getString(URI, "").toUri())
            }
        }


        binding.toolbarId.setNavigationOnClickListener {
            handleCloseScreen()
        }


        textWatcherName = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.updateField(s.toString(), "name")
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }
        textWatcherName?.let { binding?.tbName?.addTextChangedListener(it) }

        textWatcherDescription = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.updateField(s.toString(), "description")
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }
        textWatcherDescription?.let { binding?.tbDescription?.addTextChangedListener(it) }


        binding.btnSubmit.setOnClickListener({
            onPlaylistCreationClicked()
        })

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    binding.pickerImage.setImageURI(uri)
                    binding.pickerImagePreview.visibility = View.GONE
                    viewModel.updateURIField(uri, "photoUrl")
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }
        binding.pickerImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        confirmDialog = MaterialAlertDialogBuilder(requireActivity())
            .setTitle(getString(R.string.close_confirmation))
            .setMessage(getString(R.string.close_confirmation_description))
            .setNeutralButton(getString(R.string.close_confirmation_close_cancel)) { dialog, which ->

            }
            .setPositiveButton(getString(R.string.close_confirmation_complete_btn)) { dialog, which ->
                handleBack()
            }

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleCloseScreen()
            }
        })

        viewModel.getLoadingPlaylistLiveData().observe(viewLifecycleOwner) { data ->
            handlePlaylistData(data)
        }

    }

    fun handlePlaylistData(data: PlaylistCreateScreenState) {
        if (playlist == null) {
            playlist = PlaylistCreate()
        }
        if (data is PlaylistCreateScreenState.PlaylistCreateCreateNameContent) {
            playlist?.playlistName = data.name
        }
        if (data is PlaylistCreateScreenState.PlaylistCreateCreateDescriptionContent) {
            playlist?.playlistName = data.description
        }
        if (data is PlaylistCreateScreenState.PlaylistCreateCreatePhotoUrlContent) {
            playlist?.playlistImageUrl = data.photoUrl
        }
        binding.btnSubmit.isEnabled = playlist?.playlistName?.trim()?.isEmpty() != true
        this.isContentChanged = playlist?.playlistName?.trim()?.isNotEmpty() == true ||
                playlist?.playlistDescription?.trim()?.isNotEmpty() == true ||
                playlist?.playlistImageUrl?.toString()?.trim()?.isNotEmpty() == true
    }

    private fun onPlaylistCreationClicked() {
        var path: String? = null
        if (playlist?.playlistImageUrl?.toString()?.isNotEmpty() == true) {
            path = saveImageToStore()
        }

        viewModel.savePlaylist(
            playlist?.playlistName!!,
            playlist?.playlistDescription,
            path
        )

        Toast.makeText(
            requireContext(),
            "${getString(R.string.save_confirmation_playlist)} ${playlist?.playlistName} ${
                getString(
                    R.string.save_confirmation_created
                )
            }", Toast.LENGTH_SHORT
        ).show()
        handleBack()
    }

    private fun saveImageToStore(): String {
        val filePath =
            File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, UUID.randomUUID().toString() + ".jpg")
        val inputStream =
            requireActivity().contentResolver.openInputStream(playlist?.playlistImageUrl!!)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        return file.path
    }

    private fun handleCloseScreen() {
        if (!isContentChanged) {
            handleBack()
        } else {
            confirmDialog.show()
        }
    }

    private fun handleBack() {
        val fragmentToRemove = parentFragmentManager.findFragmentById(R.id.fragment_container)
        if (fragmentToRemove != null) {
            val fragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction.remove(fragmentToRemove)
            fragmentTransaction.commit()
            listener?.onFragmentRemoved()
        } else {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        textWatcherName?.let { binding?.tbName?.removeTextChangedListener(it) }
        textWatcherDescription?.let { binding?.tbDescription?.removeTextChangedListener(it) }
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(NAME, playlist?.playlistName)
        outState.putString(DESCRIPTION, playlist?.playlistDescription)
        outState.putString(URI, playlist?.playlistImageUrl?.toString())
    }

    private companion object {
        const val NAME = "NAME"
        const val DESCRIPTION = "DESCRIPTION"
        const val URI = "URI"
    }

}