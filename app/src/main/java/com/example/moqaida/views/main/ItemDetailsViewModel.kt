package com.example.moqaida.views.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moqaida.model.Items

class ItemDetailsViewModel : ViewModel() {

    val selectedLiveData = MutableLiveData<ArrayList<Items>>()



}