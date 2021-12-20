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

private const val TAG = "AddItemViewModel"

class AddItemViewModel : ViewModel() {
    private val firebaseRepo = FirebaseServiceRepository.get()

    val uploadImageLiveData = MutableLiveData<String>()
    val uploadImageErrorLiveData = MutableLiveData<String>()

    val uploadItemLiveData = MutableLiveData<String>()
    val uploadItemErrorLiveData = MutableLiveData<String>()

    fun uploadItemInfo(items: Items) {

        viewModelScope.launch(Dispatchers.IO) {

            try {
                val response = firebaseRepo.uploadItemInfo(items)
                response.addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        // we need something to observe so we use any word EX:("success")
                        uploadItemLiveData.postValue("success")

                        Log.d(TAG, "Item upload success: $response")

                    } else {
                        Log.d(TAG, task.exception!!.message.toString())
                        uploadItemErrorLiveData.postValue(task.exception!!.message)
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "Catch: ${e.message}")
                uploadItemErrorLiveData.postValue(e.message)
            }
        }

    }

 //-------------------------------------------------------------------------------------------------------------//
    // to upload Item Image to fireStorage
    fun uploadItemImage(imageUri: Uri, filename: String) {
        viewModelScope.launch(Dispatchers.IO) {

            try {
                val response = firebaseRepo.uploadItemImage(imageUri, filename)
                response.addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        // post filename to use it in upload item info
                        uploadImageLiveData.postValue(filename)
                        Log.d(TAG, "Image upload success: $response")

                    } else {
                        Log.d(TAG, task.exception!!.message.toString())
                        uploadImageErrorLiveData.postValue(task.exception!!.message)
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "Catch: ${e.message}")
                uploadImageErrorLiveData.postValue(e.message)
            }
        }
    }


    //-------------------------------------------------------------------------------------------------------------//

}