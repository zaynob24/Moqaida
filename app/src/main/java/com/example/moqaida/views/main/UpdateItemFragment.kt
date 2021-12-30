package com.example.moqaida.views.main

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.moqaida.R
import com.example.moqaida.databinding.FragmentUpdateItemBinding
import com.example.moqaida.model.Items
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.internal.entity.CaptureStrategy
import com.zhihu.matisse.internal.entity.Item

private const val TAG = "UpdateItemFragment"
class UpdateItemFragment : Fragment() {
    private lateinit var binding: FragmentUpdateItemBinding

    private val myItemsViewModel: MyItemViewModel by activityViewModels()
    private val updateItemViewModel: UpdateItemViewModel by activityViewModels()
    private lateinit var progressDialog: ProgressDialog

    private val IMAGE_PICKER = 0

    private lateinit var  name: String
    private lateinit var  location: String
    private lateinit var  yearsOfUse: String
    private lateinit var  purchasedPrice: String
    private lateinit var  estimatedPrice: String
    private lateinit var  description: String
    private var imageUri: Uri? = null


    private lateinit var currentItem: Items



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //TO make option menu show (search icon)
        setHasOptionsMenu(true)

        progressDialog = ProgressDialog(requireActivity()).also {
            it.setTitle("Loading...")
            it.setCancelable(false)
        }

        binding = FragmentUpdateItemBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observer()

        // to fill usedFor and Location menu with list of options
        fillMenuList()

        binding.UploadImageUpdate.setOnClickListener {

            showImagePicker()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // link menu with action bar
        requireActivity().menuInflater.inflate(R.menu.update_menu,menu)    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.app_bar_save -> {

                // to collect items data from all fields
                takeEntryData()

                if (checkEntryData()){ // to check if all field contain data and give error massage if not

                        progressDialog.show()

                    updateItemViewModel.updateItemInfo(currentItem)


                }else{
                    Toast.makeText(requireContext(),getText(R.string.fill_required), Toast.LENGTH_SHORT).show()
                }

            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun observer() {
        myItemsViewModel.myItemSelectedLiveData.observe(viewLifecycleOwner, { item ->
            item?.let {
                Log.d(TAG, item.toString())
                myItemsViewModel.myItemSelectedLiveData.postValue(null)


                //imageUri = item.imageUrl
                currentItem = it

                Glide
                    .with(requireContext())
                    .load(item.imageUrl)
                    .into(binding.myItemImageUpdate)

                binding.udateItemNameFiled.setText(item.itemName)
                binding.locationMenuUpdate.setText(item.location,false) //  to have a pre-selected value displayed
                binding.usedForMenuUpdate.setText(item.yearsOfUse,false)
                binding.purchasedPriceUpdate.setText(item.purchasedPrice)
                binding.estimatedPriceUpdate.setText(item.estimatedPrice)
                binding.descriptionUpdate.setText(item.description)

            }
        })

        //--------------------------------------------------------------------------//

        updateItemViewModel.updateItemLiveData.observe(viewLifecycleOwner,{

            it?.let {

                progressDialog.dismiss()
                Toast.makeText(requireActivity(), R.string.item_update_successfully, Toast.LENGTH_SHORT).show()
            }
            updateItemViewModel.updateItemLiveData.postValue(null)

        })

        updateItemViewModel.updateItemErrorLiveData.observe(viewLifecycleOwner, {
            it?.let {
                progressDialog.dismiss()
                Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
                updateItemViewModel.updateItemErrorLiveData.postValue(null)
            }
        })

        updateItemViewModel.updateImageLiveData.observe(viewLifecycleOwner,{

            it?.let {
                Toast.makeText(requireActivity(), R.string.image_upload_successfully, Toast.LENGTH_SHORT).show()
            }

            updateItemViewModel.updateImageLiveData.postValue(null)

        })

        updateItemViewModel.updateImageErrorLiveData.observe(viewLifecycleOwner, {
            it?.let {

                Log.d(TAG,"updateImageErrorLiveData"+it)
                Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
                updateItemViewModel.updateImageErrorLiveData.postValue(null)
            }
        })

    }

       //--------------------------------------------------------------------------------------------//
    // to collect items data from all fields
     fun takeEntryData() {

           name = binding.udateItemNameFiled.text.toString().trim()
           location = binding.locationMenuUpdate.text.toString().trim()
           yearsOfUse = binding.usedForMenuUpdate.text.toString().trim()
           purchasedPrice = binding.purchasedPriceUpdate.text.toString().trim()
           estimatedPrice  = binding.estimatedPriceUpdate.text.toString().trim()
           description = binding.descriptionUpdate.text.toString().trim()

           Log.d(TAG," name: $name , location: $location ,usedFore: $yearsOfUse, purchasedPrice: $purchasedPrice," +
                   "estimatedPrice: $estimatedPrice,description: $description ")

           currentItem = (Items(name,location,yearsOfUse,purchasedPrice,estimatedPrice,description,currentItem.imageUrl,
           currentItem.imageName,currentItem.userId,currentItem.documentId))

       }

        // to check if all field contain data and give error massage if not
        private fun checkEntryData() : Boolean {

        var isAllDataFilled  = true

        //check name
        if (name.isEmpty()|| name.isBlank()){
            binding.udateItemNameFiled.error = getString(R.string.required)
            isAllDataFilled = false
        }else{
            binding.udateItemNameFiled.error = null
        }

        //check location
        if (location.isEmpty()|| location.isBlank()){
            binding.locationMenuUpdateItem.error = getString(R.string.required)
            isAllDataFilled = false
        }else{
            binding.locationMenuUpdateItem.error = null
        }

        //check usedFore
        if (yearsOfUse.isEmpty()|| yearsOfUse.isBlank()){
            binding.usedForMenuUpdateItem.error = getString(R.string.required)
            isAllDataFilled = false
        }else{
            binding.usedForMenuUpdateItem.error = null
        }

        //check purchasedPrice
        if (purchasedPrice.isEmpty()|| purchasedPrice.isBlank()){
            binding.purchasedPriceUpdateItemFiled.error = getString(R.string.required)
            isAllDataFilled = false
        }else{
            binding.purchasedPriceUpdateItemFiled.error = null
        }

        //check estimatedPrice
        if (estimatedPrice.isEmpty()|| estimatedPrice.isBlank()){
            binding.estimatedPriceUpdateItemFiled.error = getString(R.string.required)
            isAllDataFilled = false
        }else{
            binding.estimatedPriceUpdateItemFiled.error = null
        }


        //check description
        if (description.isEmpty()|| description.isBlank()){
            binding.descriptionUpdateItemFiled.error = getString(R.string.required)
            isAllDataFilled = false
        }else{
            binding.descriptionUpdateItemFiled.error = null
        }

        return isAllDataFilled
        }

    //--------------------------------------------------------------------------------------------------------------//
    //--------------------------------------------------------------------------------------------------------------//

    private fun fillMenuList() {

        // fill usedFor menu with list of options
        val usedForItems = listOf(getString(R.string.used_for_option1), getString(R.string.used_for_option2),
            getString(R.string.used_for_option3),getString(R.string.used_for_option4),getString(R.string.used_for_option5))
        val usedForAdapter = ArrayAdapter(requireContext(), R.layout.list_item, usedForItems)
        (binding.usedForMenuUpdateItem.editText as? AutoCompleteTextView)?.setAdapter(usedForAdapter)

        // fill location menu with list of countries from array string
        val locationItems = resources.getStringArray(R.array.countries_array).toList()
        val locationAdapter = ArrayAdapter(requireContext(), R.layout.list_item, locationItems)
        (binding.locationMenuUpdateItem.editText as? AutoCompleteTextView)?.setAdapter(locationAdapter)
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICKER && resultCode == Activity.RESULT_OK) {

            //using Matisse library to take uri of chosen image
            imageUri = Matisse.obtainResult(data)[0]//[0] index 0 to take first index of the array of photo selected

            Glide
                .with(requireContext())
                .load(imageUri)
                .into(binding.myItemImageUpdate)

            // check if user pick new image before update it
            imageUri?.let { updateItemViewModel.updateItemImage(it,currentItem.imageName) }

        }

    }

    //--------------------------------------------------------------------------------------------------------------//

}