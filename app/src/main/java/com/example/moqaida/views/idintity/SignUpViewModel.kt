package com.example.moqaida.views.idintity

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moqaida.model.Users
import com.example.moqaida.repositories.FirebaseServiceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


private const val TAG = "SignUpViewModel"
private const val USER_COLLECTION= "users"

class SignUpViewModel:ViewModel() {
    private val firestore = FirebaseServiceRepository.get()

    val signUpLiveData = MutableLiveData<String>()
    val signUpErrorLiveData = MutableLiveData<String>()

    fun signUp(user: Users, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = firestore.signUp(user.email, password)

                response.addOnCompleteListener {
                    if (it.isSuccessful) {
                        val firestoreUser = it.result!!.user!!
                        insertUser(firestoreUser.uid, user)
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "Catch: ${e.message}")
                signUpErrorLiveData.postValue(e.message)
            }
        }
    }
    fun insertUser(userId: String, user: Users) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = firestore.insertUser(userId, user)
                response.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        signUpLiveData.postValue("Success")
                        Log.d(TAG, "SignUp success: $response")
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