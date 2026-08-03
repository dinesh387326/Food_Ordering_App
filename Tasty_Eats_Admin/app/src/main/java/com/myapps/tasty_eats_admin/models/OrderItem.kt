package com.myapps.tasty_eats_admin.models

data class OrderItem(
    val customerId: String? = null,
    val foodName: String? = null,
    val foodPrice: String? = null,
    val foodImage: String? = null,
    var foodQuantity: Int? = null
)
