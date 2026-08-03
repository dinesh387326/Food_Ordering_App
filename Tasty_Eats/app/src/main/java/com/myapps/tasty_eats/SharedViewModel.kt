package com.myapps.tasty_eats

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.myapps.tasty_eats.models.CartItem

class SharedViewModel(private val state: SavedStateHandle) : ViewModel() {
    val cartItems = MutableLiveData<ArrayList<CartItem>>()
}