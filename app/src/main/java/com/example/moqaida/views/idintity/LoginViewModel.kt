package com.example.moqaida.views.idintity

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.moqaida.model.Users
import com.example.moqaida.repositories.*
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "LoginViewModel"
// AndroidViewModel -> to use application in getSharedPreferences
class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val firebaseRepo = FirebaseServiceRepository.get()

    val loginLiveData = MutableLiveData<String>()
    val loginErrorLiveData = MutableLiveData<String>()


    // To store user details -> to use it later in bartering items
    val  sharedPref = application.getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE)
    val sharedPrefEditor = sharedPref.edit()



    fun login(email: String, password: String) {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = firebaseRepo.login(email, password)

                response.addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        // if login success -> retrieve User Info to store it in shared Pref
                        // -> to use it later in bartering items
                        retrieveUserInfo(email)

                        Log.d(TAG, "Login success: $response")

                    } else {
                        Log.d(TAG, task.exception!!.message.toString())
                        loginErrorLiveData.postValue(task.exception!!.message)
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "Catch: login ${e.message}")
                loginErrorLiveData.postValue(e.message)
            }
        }
    }

    //-------------------------------------------------------------------------------------------------//

    fun retrieveUserInfo(email: String) {

        Log.d(TAG, "retrieveUserInfo")

        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "viewModelScope:retrieveUserInfo")

//            try {
//
//                Log.d(TAG, "try:retrieveUserInfo")
//
//                val response = firebaseRepo.retrieveUserInfo(email)
//
//                for (document in response.documents) {
//                    user = document.toObject<Users>()!!
//                    Log.d(TAG, "User; $user")
//                }
//
//                // get User Info to store it in shared Pref
//                sharedPrefEditor.putString(USER_NAME,user!!.fullName)
//                sharedPrefEditor.putString(USER_EMAIL, user.email)
//                sharedPrefEditor.putString(USER_PHONE, user.phoneNumber)
//
//                sharedPrefEditor.commit()
//
//                // post success to live data
//                loginLiveData.postValue("success login")
//
//                Log.d(TAG,"USER_NAME ${user.fullName} , USER_NAME ${user.email} , USER_NAME ${user.phoneNumber}")
//
//
//            } catch (e: Exception) {
//                Log.d(TAG, "Catch retrieveUserInfo: ${e.message}")
//                loginErrorLiveData.postValue(e.message)
//
//            }


            try {
                val response = firebaseRepo.retrieveUserInfo()

                response.addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.d(TAG, "Get user info error: ${error.message}")
                        return@addSnapshotListener
                    }

                    value?.let {
                        val currentUser = value.toObject(Users::class.java)!!

                 // get User Info to store it in shared Pref
                sharedPrefEditor.putString(USER_NAME,currentUser.fullName)
                sharedPrefEditor.putString(USER_EMAIL, currentUser.email)
                sharedPrefEditor.putString(USER_PHONE, currentUser.phoneNumber)

                sharedPrefEditor.commit()

                // post success to live data
                loginLiveData.postValue("success login")
                        Log.d(TAG, "User: $currentUser")
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "Error - catch: ${e.message}")
                loginErrorLiveData.postValue(e.message)
            }
        }
    }
}