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

private const val TAG = "MyItemViewModel"
class MyItemViewModel : ViewModel() {

    private val firebaseRepo = FirebaseServiceRepository.get()


    val retrieveMyItemsLiveData = MutableLiveData<ArrayList<Items>>()
    val retrieveMyItemsErrorLiveData = MutableLiveData<String>()
    val myItemSelectedLiveData = MutableLiveData<Items>()


    val deleteMyItemsLiveData = MutableLiveData<String>()
    val deleteMyItemsErrorLiveData = MutableLiveData<String>()

    //-----------------------------------------------------------------------------------------------------//


    //To retrieve current user Items
    fun retrieveMyItems() {

        val myItemArrayList: ArrayList<Items> = arrayListOf()

        viewModelScope.launch(Dispatchers.IO) {
            try {

                val response = firebaseRepo.retrieveMyItems()

                for (document in response.documents) {
                    Log.d(TAG, "document" + document.toString())

                    val item = document.toObject<Items>()
                    // Log.d(TAG, "item" + item.toString())

                    if (item != null) {
                        item.documentId = document.id

                        Log.d(TAG, item.documentId)
                    }

                    item?.let { myItemArrayList.add(it) }

                }

                // Log.d(TAG, myItemArrayList.toString())
                retrieveMyItemsLiveData.postValue(myItemArrayList)

                // Log.d(TAG, "retrieveMyItems success: $response")


            } catch (e: Exception) {
                Log.d(TAG, "Catch: ${e.message}")
                retrieveMyItemsErrorLiveData.postValue(e.message)
            }

        }
    }

    //-----------------------------------------------------------------------------------------------------//

    fun deleteMyItem(items: Items) {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = firebaseRepo.deleteMyItem(items)
                response.addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        deleteMyItemsLiveData.postValue("Item delete success")
                        // to delete image from fireStorage
                        firebaseRepo.deleteImage(items.imageName)
                        Log.d(TAG, "Item delete success $response")

                    } else {
                        Log.d(TAG, task.exception!!.message.toString())
                        deleteMyItemsErrorLiveData.postValue(task.exception!!.message)
                    }
                }
            } catch (e: java.lang.Exception) {
                Log.d(TAG, "Catch: ${e.message}")
                deleteMyItemsErrorLiveData.postValue(e.message)
            }
        }
    }
}