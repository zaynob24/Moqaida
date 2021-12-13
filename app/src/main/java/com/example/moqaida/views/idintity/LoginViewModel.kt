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
class LoginViewModel : ViewModel() {

    private val firestore = FirebaseServiceRepository.get()

    val loginLiveData = MutableLiveData<String>()
    val loginErrorLiveData = MutableLiveData<String>()

    fun login(email: String, password: String) {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = firestore.login(email, password)

                response.addOnCompleteListener {
                    if (it.isSuccessful) {
                        loginLiveData.postValue("Login is success")
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "Catch: ${e.message}")
                loginErrorLiveData.postValue(e.message)
            }
        }
    }
}