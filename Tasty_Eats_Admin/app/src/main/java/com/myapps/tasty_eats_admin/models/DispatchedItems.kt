package com.myapps.tasty_eats_admin.models

data class DispatchedItems(
    val customerName: String? = null,
    val customerAddress: String? = null,
    val customerEmail: String? = null,
    val customerPhone: String? = null,
    val foodName: String? = null,
    val foodPrice: String? = null,
    val foodImage: String? = null,
    var foodQuantity: Int? = null,
    val status: String? = null
)
