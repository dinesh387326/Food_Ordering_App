package com.myapps.tasty_eats_admin.models

data class PendingItem(
    val customerName: String? = null,
    val foodPrice: String? = null,
    val foodImage: String? = null,
    var foodQuantity: Int? = null
)
