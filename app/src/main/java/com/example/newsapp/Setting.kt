package com.example.newsapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import com.example.newsapp.R

class Setting : Fragment() {

    private val PICK_IMAGE_REQUEST = 1
    private lateinit var selectedImageUri: Uri
    companion object {
        var profileImageUri: Uri? = null
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)
        val toggleDarkMode = view.findViewById<SwitchCompat>(R.id.toggleDarkMode)
        val selectImageButton = view.findViewById<Button>(R.id.changeImageButton)

        selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        toggleDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Switch to dark mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                // Switch to light mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            // Delay the recreation of the activity to apply the new theme smoothly
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data!!
            profileImageUri = data.data!!
            val profileImageView = view?.findViewById<ImageView>(R.id.profileImageView)
            profileImageView?.setImageURI(selectedImageUri)


        }
    }
}
