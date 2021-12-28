package com.example.moqaida.views.dialogs

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moqaida.model.Items
import com.example.moqaida.model.Requests
import com.example.moqaida.repositories.FirebaseServiceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

private const val TAG = "BarteringDialogViewMode"
class BarteringDialogViewModel: ViewModel()  {

    private val firebaseRepo = FirebaseServiceRepository.get()

    val sendBarteringRequestLiveData = MutableLiveData<String>()
    val sendBarteringRequestErrorLiveData = MutableLiveData<String>()



    fun sendBarteringRequest(request: Requests) {

        Log.d(TAG, "sendBarteringRequest")

        viewModelScope.launch(Dispatchers.IO) {

            try {
                val response = firebaseRepo.sendBarteringRequest(request)
                response.addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        // we need something to observe so we use any word EX:("success")
                        sendBarteringRequestLiveData.postValue("success")

                        Log.d(TAG, "Request sent successfully: $response")

                    } else {
                        Log.d(TAG, "sendBarteringRequest: else")

                        Log.d(TAG, task.exception!!.message.toString())
                        sendBarteringRequestErrorLiveData.postValue(task.exception!!.message)
                    }
                }
            } catch (e: Exception) {

                Log.d(TAG, "Catch: ${e.message}")
                sendBarteringRequestErrorLiveData.postValue(e.message)
            }
        }

    }


}