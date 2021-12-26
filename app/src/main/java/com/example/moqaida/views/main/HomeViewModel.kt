package com.example.moqaida.views.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moqaida.model.Items
import com.example.moqaida.repositories.FirebaseServiceRepository
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "HomeViewModel"

class HomeViewModel : ViewModel() {

    private val firebaseRepo = FirebaseServiceRepository.get()


    val retrieveItemsLiveData = MutableLiveData<ArrayList<Items>>()
    val retrieveItemsErrorLiveData = MutableLiveData<String>()
    val selectedItemsLiveData = MutableLiveData<Items>()


    fun retrieveItems() {

        val itemArrayList: ArrayList<Items> = arrayListOf()

        viewModelScope.launch(Dispatchers.IO) {
            try {

                val response = firebaseRepo.retrieveItems()

//                response.addOnCompleteListener { querySnapshot ->
//
//                    if (querySnapshot.isSuccessful) {

                for (document in response.documents) {
                    Log.d(TAG, "document" + document.toString())

                    val item = document.toObject<Items>()
                    Log.d(TAG, "item" + item.toString())


                    item?.let { itemArrayList.add(it) }

                }

                Log.d(TAG, itemArrayList.toString())
                retrieveItemsLiveData.postValue(itemArrayList)

                Log.d(TAG, "retrieveItems success: $response")

//                    } else {
//                        Log.d(TAG, querySnapshot.exception!!.message.toString())
//                        retrieveItemsErrorLiveData.postValue(querySnapshot.exception!!.message)
//                    }



        } catch (e: Exception) {
            Log.d(TAG, "Catch: ${e.message}")
            retrieveItemsErrorLiveData.postValue(e.message)
        }

        }
    }

}