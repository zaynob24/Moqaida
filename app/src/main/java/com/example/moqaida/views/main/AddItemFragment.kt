package com.example.moqaida.views.main

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast

import androidx.fragment.app.activityViewModels
import com.example.moqaida.MainActivity
import com.example.moqaida.R
import com.example.moqaida.databinding.FragmentAddItemBinding
import com.example.moqaida.util.Permissions
import com.example.moqaida.views.dialogs.ImageDialogFragment
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
import kotlin.math.log

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

    private lateinit var  name: String
    private lateinit var  location: String
    private lateinit var  usedFore: String
    private lateinit var  purchasedPrice: String
    private lateinit var  estimatedPrice: String
    private lateinit var  description: String
    private var  imageNameVisibility: Int = 4 // 4 for invisible - 0 for visible


    //-----------------------------------------------------------------------------------//

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

    //-----------------------------------------------------------------------------------//


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


           // to make editText accept number only (for price)
        binding.purchasedPriceAddItem.inputType = InputType.TYPE_CLASS_NUMBER
        binding.estimatedPriceAddItem.inputType = InputType.TYPE_CLASS_NUMBER


        //  show image picker for user to chose item image
        binding.UploadImageTextView.setOnClickListener {
            Permissions.checkPermission(requireContext(), requireActivity())
            showImagePicker()
        }


        // to fill usedFor and Location menu with list of options
        fillMenuList()

        //-----------------------------------------------------------------//

            // save Item
        binding.saveAddItemButton.setOnClickListener {

              takeEntryData() // to collect items data from all fields

             if (checkEntryData()){ // to check if all field contain data and give error massage if not

                 //TODO : save data to fire base
             }else{
                 Toast.makeText(requireContext(),getText(R.string.fill_required), Toast.LENGTH_SHORT).show()
             }


//            imageUri?.let {
//                progressDialog.show()
//                // I pass time in millis to use it as filename of the image
//                // so I be sure it is unique in firestorge(Duplicated name with replace the old image instead of add new one!)
//                addItemViewModel.uploadItemImage(it, System.currentTimeMillis().toString())
//            }?:Toast.makeText(requireContext(),R.string.no_image_massage, Toast.LENGTH_LONG).show()

        }
        //-----------------------------------------------------------------//


        //open dialog tha show full screen image
        binding.imageNameTextView.setOnClickListener {

            val activity = requireContext() as? MainActivity
            imageUri?.let { uri ->
                ImageDialogFragment(uri).show(
                    activity!!.supportFragmentManager,
                    "DetailsDialogFragment"
                )
            }
        }

//        binding.saveAddItemButton.setOnClickListener {
//            downloadImage(fileName)
//        }

        observer()

    }


    //--------------------------------------------------------------------------------------------------------------//
    //--------------------------------------------------------------------------------------------------------------//

    private fun fillMenuList() {

        // fill usedFor menu with list of options
        val usedForItems = listOf(getString(R.string.used_for_option1), getString(R.string.used_for_option2),
            getString(R.string.used_for_option3),getString(R.string.used_for_option4),getString(R.string.used_for_option5))
        val usedForAdapter = ArrayAdapter(requireContext(), R.layout.list_item, usedForItems)
        (binding.usedForMenuAddItem.editText as? AutoCompleteTextView)?.setAdapter(usedForAdapter)


        val locationItems = resources.getStringArray(R.array.countries_array).toList()
        val locationAdapter = ArrayAdapter(requireContext(), R.layout.list_item, locationItems)
        (binding.locationMenuAddItem.editText as? AutoCompleteTextView)?.setAdapter(locationAdapter)
    }
    //--------------------------------------------------------------------------------------------------------------//

   // to collect items data from all fields
    private fun takeEntryData() {
         name = binding.itemNameAddItem.text.toString().trim()
         location = binding.locationMenuEditeTextAddItem.text.toString().trim()
         usedFore = binding.usedForMEditeTextenuAddItem.text.toString().trim()
         purchasedPrice = binding.purchasedPriceAddItem.text.toString().trim()
         estimatedPrice  = binding.estimatedPriceAddItem.text.toString().trim()
         description = binding.descriptionAddItem.text.toString().trim()
         imageNameVisibility = binding.imageNameTextView.visibility

        Log.d(TAG,"name: $name , location: $location ,usedFore: $usedFore, purchasedPrice: $purchasedPrice," +
                "estimatedPrice: $estimatedPrice,description: $description imageNameVisibility: $imageNameVisibility")
    }

    // to check if all field contain data and give error massage if not
    private fun checkEntryData() : Boolean {
           var isAllDataFilled  = true

        //check name
        if (name.isEmpty()|| name.isBlank()){
            binding.itemNameAddItemfiled.error = getString(R.string.required)
            isAllDataFilled = false
        }else{
            binding.itemNameAddItemfiled.error = null
        }


        //check image
        if (imageNameVisibility == 4){  // 0 for visible - 4 for invisible
            binding.imageRequiredText.visibility = View.VISIBLE
            isAllDataFilled = false
        }else{
            binding.imageRequiredText.visibility = View.INVISIBLE
        }

        //check location
        if (location.isEmpty()|| location.isBlank()){
            binding.locationMenuAddItem.error = getString(R.string.required)
            isAllDataFilled = false
        }else{
            binding.locationMenuAddItem.error = null
        }

        //check usedFore
        if (usedFore.isEmpty()|| usedFore.isBlank()){
            binding.usedForMenuAddItem.error = getString(R.string.required)
            isAllDataFilled = false
        }else{
            binding.usedForMenuAddItem.error = null
        }

        //check purchasedPrice
        if (purchasedPrice.isEmpty()|| purchasedPrice.isBlank()){
            binding.purchasedPriceAddItemFiled.error = getString(R.string.required)
            isAllDataFilled = false
        }else{
            binding.purchasedPriceAddItemFiled.error = null
        }

        //check estimatedPrice
        if (estimatedPrice.isEmpty()|| estimatedPrice.isBlank()){
            binding.estimatedPriceAddItemFiled.error = getString(R.string.required)
            isAllDataFilled = false
        }else{
            binding.estimatedPriceAddItemFiled.error = null
        }


        //check description
        if (description.isEmpty()|| description.isBlank()){
            binding.descriptionAddItemFiled.error = getString(R.string.required)
            isAllDataFilled = false
        }else{
            binding.descriptionAddItemFiled.error = null
        }

        return isAllDataFilled
    }

    //--------------------------------------------------------------------------------------------------------------//

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICKER && resultCode == Activity.RESULT_OK) {

             //my code
            imageUri = Matisse.obtainResult(data)[0] //[0] index 0 to take first index of the array of photo selected

            binding.imageRequiredText.visibility = View.INVISIBLE
            binding.imageNameTextView.visibility = View.VISIBLE

 //            val imagePath = Matisse.obtainPathResult(data)[0] //[0] index 0 to take first index of the array of photo selected
//            val imageFile = File(imagePath)
//            binding.addItemImageView.setImageURI(imageUri)
        }

    }

    //--------------------------------------------------------------------------------------------------------------//

    //TODO: change place of this code -no need here -
    // I use it just for testing of downloadImage with it is file name from fire storage
    private fun downloadImage(filename: String) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val maxDownloadSize = 5L * 1024 * 1024
            val bytes = imageRef.child("images/$filename").getBytes(maxDownloadSize).await()
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            withContext(Dispatchers.Main) {
                //binding.imagetest.setImageBitmap(bmp)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
    //--------------------------------------------------------------------------------------------------------------//

    // showing ImagePicker using Matisse library
    fun showImagePicker() {
        Matisse.from(this)
            .choose(MimeType.ofImage(), false) // image or image and video or whatever
            .captureStrategy(CaptureStrategy(true, "com.example.moqaida"))
            .forResult(IMAGE_PICKER)
    }

    //--------------------------------------------------------------------------------------------------------------//

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
                addItemViewModel.uploadImageLiveData.postValue(null)

                saveItem()
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
    //--------------------------------------------------------------------------------------------------------------//

    private fun saveItem() {

    }
    //--------------------------------------------------------------------------------------------------------------//

}