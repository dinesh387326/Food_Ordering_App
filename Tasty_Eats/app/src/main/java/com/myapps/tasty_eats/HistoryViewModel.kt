package com.myapps.tasty_eats

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class HistoryViewModel(private val state: SavedStateHandle) : ViewModel() {
    val nameList = MutableLiveData<MutableList<String>>(mutableListOf())
    val priceList = MutableLiveData<MutableList<String>>(mutableListOf())
    val imageList = MutableLiveData<MutableList<Bitmap>>(mutableListOf())

    fun addItem(name: String, price: String, image: Bitmap) {
        nameList.value?.add(name)
        priceList.value?.add(price)
        imageList.value?.add(image)

        // Force LiveData update to trigger observers
        nameList.value = nameList.value
        priceList.value = priceList.value
        imageList.value = imageList.value
    }
}