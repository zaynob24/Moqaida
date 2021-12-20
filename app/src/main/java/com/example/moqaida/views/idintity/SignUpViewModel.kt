package com.example.moqaida.views.idintity

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moqaida.R
import com.example.moqaida.model.Users
import com.example.moqaida.repositories.FirebaseServiceRepository
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


private const val TAG = "SignUpViewModel"

class SignUpViewModel:ViewModel() {
    private val firebaseRepo = FirebaseServiceRepository.get()

    val signUpLiveData = MutableLiveData<String>()
    val signUpErrorLiveData = MutableLiveData<String>()

    fun signUp(user: Users, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = firebaseRepo.signUp(user.email, password)

                response.addOnCompleteListener {
                    if (it.isSuccessful) {
                        insertUser(user)
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "Catch: ${e.message}")
                signUpErrorLiveData.postValue(e.message)
            }
        }
    }
    private fun insertUser(user: Users) {


        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(user.fullName)
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                firebaseRepo.firebaseAuth.currentUser?.updateProfile(profileUpdates)?.await()

            } catch(e: Exception) {
                }
            }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = firebaseRepo.insertUser(user)
                response.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        signUpLiveData.postValue("Success")
                        Log.d(TAG, "SignUp success: $response")
                        Log.d(TAG, "SignUp success: $response"+response.result.toString()+task.result.toString()+task)

                    } else {
                        Log.d(TAG, "Fail SignUp")
                        signUpErrorLiveData.postValue(response.exception!!.message)
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "Catch: ${e.message}")
                signUpErrorLiveData.postValue(e.message)
            }
        }

    }


}
