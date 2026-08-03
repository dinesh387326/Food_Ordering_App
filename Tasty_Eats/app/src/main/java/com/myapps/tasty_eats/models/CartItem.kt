package com.myapps.tasty_eats.models

data class CartItem(
    val shopId: String? = null,
    val foodName: String? = null,
    val foodPrice: String? = null,
    val foodImage: String? = null,
    var foodQuantity: Int? = null
)
