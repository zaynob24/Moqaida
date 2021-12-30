package com.example.moqaida.views.main

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moqaida.model.Items
import com.example.moqaida.repositories.FirebaseServiceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

private const val TAG = "UpdateItemViewModel"
class UpdateItemViewModel:ViewModel() {

    private val firebaseRepo = FirebaseServiceRepository.get()

    val updateImageLiveData = MutableLiveData<String>()
    val updateImageErrorLiveData = MutableLiveData<String>()

    val updateItemLiveData = MutableLiveData<String>()
    val updateItemErrorLiveData = MutableLiveData<String>()

    fun updateItemInfo(items: Items) {

        viewModelScope.launch(Dispatchers.IO) {

            try {
                Log.d(TAG, "updateItemInfo ${items.documentId}")

                val response = firebaseRepo.updateItem(items)

                response.addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        // we need something to observe so we use any word EX:("success")
                        updateItemLiveData.postValue("success")

                        Log.d(TAG, "Item update success: $response")

                    } else {
                        Log.d(TAG, task.exception!!.message.toString())
                        updateItemErrorLiveData.postValue(task.exception!!.message)
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "Catch: ${e.message}")
                updateItemErrorLiveData.postValue(e.message)
            }
        }

    }

    //-------------------------------------------------------------------------------------------------------------//
    // to upload Item Image to fireStorage
    fun updateItemImage(imageUri: Uri, filename: String) {
        viewModelScope.launch(Dispatchers.IO) {

            try {
                val response = firebaseRepo.uploadItemImage(imageUri, filename)
                response.addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        // post filename to use it in upload item info
                        updateImageLiveData.postValue(filename)
                        Log.d(TAG, "Image upload success: $response")

                    } else {
                        Log.d(TAG, task.exception!!.message.toString())
                        updateImageErrorLiveData.postValue(task.exception!!.message)
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "Catch: ${e.message}")
                updateImageErrorLiveData.postValue(e.message)
            }
        }
    }





    //-------------------------------------------------------------------------------------------------------------//

}