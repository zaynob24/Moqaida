package com.example.moqaida.views.main

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moqaida.repositories.FirebaseServiceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

private const val TAG = "AddItemViewModel"

class AddItemViewModel : ViewModel() {
    private val firestorage = FirebaseServiceRepository.get()


    val uploadImageLiveData = MutableLiveData<String>()

    val uploadImageErrorLiveData = MutableLiveData<String>()


    fun uploadItemImage(imageUri: Uri, filename: String) {
        viewModelScope.launch(Dispatchers.IO) {

            try {
                val response = firestorage.uploadItemImage(imageUri, filename)
                response.addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        // post user id to use it in sharedPref
                        uploadImageLiveData.postValue(filename)

                        Log.d(TAG, "SignUp success: $response")

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
}