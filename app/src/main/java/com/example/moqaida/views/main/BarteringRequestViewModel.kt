package com.example.moqaida.views.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moqaida.model.Items
import com.example.moqaida.model.Requests
import com.example.moqaida.repositories.FirebaseServiceRepository
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

private const val TAG = "BarteringRequestViewMod"
class BarteringRequestViewModel:ViewModel() {

    private val firebaseRepo = FirebaseServiceRepository.get()

    val barteringRequestLiveData = MutableLiveData<ArrayList<Requests>>()
    val barteringRequestErrorLiveData = MutableLiveData<String>()


    val deleteRequestLiveData = MutableLiveData<String>()
    val deleteRequestErrorLiveData = MutableLiveData<String>()

         // retrieve Bartering Request
    fun retrieveBarteringRequest() {

        Log.d(TAG, "retrieveBarteringRequest")

        viewModelScope.launch(Dispatchers.IO) {

            try {
                val response = firebaseRepo.retrieveBarteringRequest()

                val requestArrayList: ArrayList<Requests> = arrayListOf()

                for (document in response.documents) {
                    Log.d(TAG, "document" + document.toString())

                    val request = document.toObject<Requests>()
                     Log.d(TAG, "request" + request.toString())

                    if (request != null) {

                        request.requestID = document.id

                        Log.d(TAG, request.requestID)
                    }

                    request?.let {

                        requestArrayList.add(it)
                        Log.d(TAG, requestArrayList.toString())

                    }

                }

                //Log.d(TAG, itemArrayList.toString())
                barteringRequestLiveData.postValue(requestArrayList)

                Log.d(TAG, "retrieveItems success: $response")

            } catch (e: Exception) {
                Log.d(TAG, "Catch: ${e.message}")
                barteringRequestErrorLiveData.postValue(e.message)
            }

        }


    }

    //----------------------------------------------------------------------------------------//

    // delete Bartering Request

    fun deleteBarteringRequest(request: Requests) {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = firebaseRepo.deleteBarteringRequest(request)
                response.addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        deleteRequestLiveData.postValue("Request delete success")

                        Log.d(TAG, "Request delete success $response")

                    } else {
                        Log.d(TAG, task.exception!!.message.toString())
                        deleteRequestErrorLiveData.postValue(task.exception!!.message)
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "Catch: ${e.message}")
                deleteRequestErrorLiveData.postValue(e.message)
            }
        }
    }
}