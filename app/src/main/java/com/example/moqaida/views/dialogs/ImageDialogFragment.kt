package com.example.moqaida.views.dialogs

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.moqaida.R
import com.example.moqaida.databinding.FragmentImageDialogBinding
import com.example.moqaida.databinding.FragmentSignUpBinding

class ImageDialogFragment(val imageUri: Uri) : DialogFragment() {

    private lateinit var binding: FragmentImageDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImageDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fullScrrenImageDialog.setImageURI(imageUri)

        binding.closeImageIcon.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
       // val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        dialog!!.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}