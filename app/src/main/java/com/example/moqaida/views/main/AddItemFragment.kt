package com.example.moqaida.views.main

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
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
import androidx.navigation.fragment.findNavController
import com.example.moqaida.MainActivity
import com.example.moqaida.R
import com.example.moqaida.databinding.FragmentAddItemBinding
import com.example.moqaida.model.Items
import com.example.moqaida.repositories.SHARED_PREF_FILE
import com.example.moqaida.repositories.USER_ID
import com.example.moqaida.util.Permissions
import com.example.moqaida.views.dialogs.ImageDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.internal.entity.CaptureStrategy
import com.zhihu.matisse.internal.entity.Item
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
    private var imageFileName = ""
    val  firebaseAuth = FirebaseAuth.getInstance()

    private val IMAGE_PICKER = 0
    private lateinit var binding: FragmentAddItemBinding
    private val addItemViewModel: AddItemViewModel by activityViewModels()

    private lateinit var progressDialog: ProgressDialog

    private lateinit var  name: String
    private lateinit var  location: String
    private lateinit var  yearsOfUse: String
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

        checkLoggedInState()

        // if user not login
        binding.loginTV.setOnClickListener {
            findNavController().navigate(R.id.action_addItemFragment_to_loginFragment)
        }

        //-----------------------------------------//
           // to fill usedFor and Location menu with list of options
        fillMenuList()

           // to make editText accept number only (for price)
        binding.purchasedPriceAddItem.inputType = InputType.TYPE_CLASS_NUMBER
        binding.estimatedPriceAddItem.inputType = InputType.TYPE_CLASS_NUMBER


        //-----------------------------------------------------------------//

        //  show image picker for user to chose item image
        binding.UploadImageTextView.setOnClickListener {

            Permissions.checkPermission(requireContext(), requireActivity())
            showImagePicker()     // this function showing ImagePicker using Matisse library then give imageUri of chosen image
        }


        //-----------------------------------------------------------------//

            // save Item
        binding.saveAddItemButton.setOnClickListener {

              takeEntryData() // to collect items data from all fields

             if (checkEntryData()){ // to check if all field contain data and give error massage if not

                imageUri?.let {
                progressDialog.show()
                // pass time in millis to use it as filename of the image
                // to be sure it is unique in firestorge(Duplicated name with replace the old image instead of add new one!)
                addItemViewModel.uploadItemImage(it, System.currentTimeMillis().toString())
            }?:Toast.makeText(requireContext(),R.string.no_image_massage, Toast.LENGTH_LONG).show()

             }else{
                 Toast.makeText(requireContext(),getText(R.string.fill_required), Toast.LENGTH_SHORT).show()
             }


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

        //-----------------------------------------------------------------//

        observer()

        //        binding.saveAddItemButton.setOnClickListener {
//            downloadImage(fileName)
//        }

    }


    //--------------------------------------------------------------------------------------------------------------//
    //--------------------------------------------------------------------------------------------------------------//

    private fun fillMenuList() {

        // fill usedFor menu with list of options
        val usedForItems = listOf(getString(R.string.used_for_option1), getString(R.string.used_for_option2),
            getString(R.string.used_for_option3),getString(R.string.used_for_option4),getString(R.string.used_for_option5))
        val usedForAdapter = ArrayAdapter(requireContext(), R.layout.list_item, usedForItems)
        (binding.usedForMenuAddItem.editText as? AutoCompleteTextView)?.setAdapter(usedForAdapter)

        // fill location menu with list of countries from array string
        val locationItems = resources.getStringArray(R.array.countries_array).toList()
        val locationAdapter = ArrayAdapter(requireContext(), R.layout.list_item, locationItems)
        (binding.locationMenuAddItem.editText as? AutoCompleteTextView)?.setAdapter(locationAdapter)
    }
    //--------------------------------------------------------------------------------------------------------------//

   // to collect items data from all fields
    private fun takeEntryData() {
         name = binding.itemNameAddItem.text.toString().trim()
         location = binding.locationMenuEditeTextAddItem.text.toString().trim()
         yearsOfUse = binding.usedForMEditeTextenuAddItem.text.toString().trim()
         purchasedPrice = binding.purchasedPriceAddItem.text.toString().trim()
         estimatedPrice  = binding.estimatedPriceAddItem.text.toString().trim()
         description = binding.descriptionAddItem.text.toString().trim()
         imageNameVisibility = binding.imageNameTextView.visibility

        Log.d(TAG,"name: $name , location: $location ,usedFore: $yearsOfUse, purchasedPrice: $purchasedPrice," +
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
        if (yearsOfUse.isEmpty()|| yearsOfUse.isBlank()){
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

             //using Matisse library to take uri of chosen image
            imageUri = Matisse.obtainResult(data)[0] //[0] index 0 to take first index of the array of photo selected

            binding.imageRequiredText.visibility = View.INVISIBLE
            binding.imageNameTextView.visibility = View.VISIBLE

            //TODO erase this code if no need or use it (for camera)

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
        // after uploading item image to fireStorage finish
        // image upload successfully
        addItemViewModel.uploadImageLiveData.observe(viewLifecycleOwner, {
            it?.let {

                imageFileName = it //name of image in fireStorage (the name is: currentTimeMillis)
                val imageUrl = "https://firebasestorage.googleapis.com/v0/b/moqaida-z.appspot.com/o/images%2F$imageFileName?alt=media&token=c1cbbd99-21e8-4887-b309-2388412dea6f"

                addItemViewModel.uploadImageLiveData.postValue(null)

                // save item details (name,image,price and so on )to fireStore
                addItemViewModel.uploadItemInfo(Items(name,location,yearsOfUse,purchasedPrice,estimatedPrice,description,imageUrl,imageFileName,firebaseAuth.currentUser!!.uid))

            }
        })

        addItemViewModel.uploadImageErrorLiveData.observe(viewLifecycleOwner, {
            it?.let {
                progressDialog.dismiss()
                Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
                addItemViewModel.uploadImageErrorLiveData.postValue(null)
            }
        })
        //-----------------------------------------------------------------------------------//

        // after uploading item to fireStore finish
        //  upload successfully
        addItemViewModel.uploadItemLiveData.observe(viewLifecycleOwner,{
            it?.let {

                 progressDialog.dismiss()  // To close the progress Dialog after uploading image
                 Toast.makeText(requireActivity(), R.string.item_upload_successfully, Toast.LENGTH_SHORT).show()
                addItemViewModel.uploadItemLiveData.postValue(null)
                findNavController().popBackStack()
            }
        })


        addItemViewModel.uploadItemErrorLiveData.observe(viewLifecycleOwner, {
            it?.let {
                progressDialog.dismiss()
                Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
                addItemViewModel.uploadItemErrorLiveData.postValue(null)
            }
        })

    }


    //--------------------------------------------------------------------------------------------------------------//

    private fun checkLoggedInState() {

        firebaseAuth.currentUser?.let {

            // user logged in!
            binding.addItemLayout.visibility = View.VISIBLE
            binding.addItemNotLoginLayout.visibility = View.GONE

        }?:run {
            // user are not logged in
            binding.addItemNotLoginLayout.visibility = View.VISIBLE
            binding.addItemLayout.visibility = View.GONE

        }

    }
}