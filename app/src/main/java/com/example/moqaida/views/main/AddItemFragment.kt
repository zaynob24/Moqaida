package com.example.moqaida.views.main

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.ImageViewCompat

import androidx.fragment.app.activityViewModels
import com.example.moqaida.R
import com.example.moqaida.databinding.FragmentAddItemBinding
import com.example.moqaida.util.Permissions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.internal.entity.CaptureStrategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//uploadImageToStorage && Downloading Files - Firebase Cloud Storage
//https://github.com/philipplackner/FirebaseStorage/blob/Downloading-Files/app/src/main/java/com/androiddevs/firebasestorage/MainActivity.kt

private const val TAG = "AddItemFragment"
class AddItemFragment : Fragment() {

    val imageRef = Firebase.storage.reference
    private var imageUri: Uri? = null
    private var fileName = ""

    private val IMAGE_PICKER = 0
    private lateinit var binding: FragmentAddItemBinding
    private val addItemViewModel: AddItemViewModel by activityViewModels()

    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        progressDialog = ProgressDialog(requireActivity())
        progressDialog.setTitle("Loading...")
        progressDialog.setCancelable(false)

        binding = FragmentAddItemBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // we show image picker for user to chose item image
        binding.addItemImageView.setOnClickListener {
            Permissions.checkPermission(requireContext(), requireActivity())
            showImagePicker()
        }

        binding.UploadImageTextView.setOnClickListener {


            imageUri?.let {
                progressDialog.show()
                // I pass time in millis to use it as filename of the image
                // so I be sure it is unique in firestorge(Duplicated name with replace the old image instead of add new one!)
                addItemViewModel.uploadItemImage(it, System.currentTimeMillis().toString())
            }?:Toast.makeText(requireContext(),R.string.no_image_massage, Toast.LENGTH_LONG).show()


        }

        binding.saveAddItemButton.setOnClickListener {
            downloadImage(fileName)
        }

        observer()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICKER && resultCode == Activity.RESULT_OK) {

            imageUri = Matisse.obtainResult(data)[0] //[0] index 0 to take first index of the array of photo selected
            ImageViewCompat.setImageTintList(binding.addItemImageView, null)
            binding.addItemImageView.setImageURI(imageUri)
            binding.imageNameTextView.text = ""

        }
    }


    //TODO: change place of this code -no need here -
    // I use it just for testing of downloadImage with it is file name from fire storage
    private fun downloadImage(filename: String) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val maxDownloadSize = 5L * 1024 * 1024
            val bytes = imageRef.child("images/$filename").getBytes(maxDownloadSize).await()
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            withContext(Dispatchers.Main) {
                binding.imagetest.setImageBitmap(bmp)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
            }
        }
    }


    // showing ImagePicker using Matisse library
    fun showImagePicker() {
        Matisse.from(this)
            .choose(MimeType.ofImage(), false) // image or image and video or whatever
            .capture(true) // to show camera
            .captureStrategy(CaptureStrategy(true, "com.example.moqaida"))
            .forResult(IMAGE_PICKER)
    }

    @SuppressLint("SetTextI18n")
    private fun observer() {
        // after uploading item image finish
        addItemViewModel.uploadImageLiveData.observe(viewLifecycleOwner, {
            it?.let {
                progressDialog.dismiss()  // To close the progress Dialog after uploading image
                Toast.makeText(
                    requireActivity(),
                    R.string.image_upload_successfully,
                    Toast.LENGTH_SHORT
                ).show()

                fileName = it
                binding.imageNameTextView.text = "image: (${imageUri.toString()})"
                addItemViewModel.uploadImageLiveData.postValue(null)
            }
        })

        addItemViewModel.uploadImageErrorLiveData.observe(viewLifecycleOwner, {
            it?.let {
                progressDialog.dismiss()
                Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
                addItemViewModel.uploadImageErrorLiveData.postValue(null)
            }
        })
    }
}