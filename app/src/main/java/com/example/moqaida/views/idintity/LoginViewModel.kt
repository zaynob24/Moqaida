package com.example.moqaida.views.idintity

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moqaida.repositories.FirebaseServiceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "LoginViewModel"
class LoginViewModel : ViewModel() {

    private val firebaseRepo = FirebaseServiceRepository.get()

    val loginLiveData = MutableLiveData<String>()
    val loginErrorLiveData = MutableLiveData<String>()

    fun login(email: String, password: String) {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = firebaseRepo.login(email, password)

                response.addOnCompleteListener {task->
                    if (task.isSuccessful) {
                        // post user id to use it in sharedPref
                        loginLiveData.postValue(firebaseRepo.firebaseAuth.currentUser!!.uid)

                        Log.d(TAG, "SignUp success: $response")

                    }else{
                        Log.d(TAG, task.exception!!.message.toString())
                        loginErrorLiveData.postValue(task.exception!!.message)
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "Catch: ${e.message}")
                loginErrorLiveData.postValue(e.message)
            }
        }
    }
}