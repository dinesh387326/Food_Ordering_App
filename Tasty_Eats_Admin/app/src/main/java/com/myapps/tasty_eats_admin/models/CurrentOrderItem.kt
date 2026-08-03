package com.myapps.tasty_eats_admin.models

data class CurrentOrderItem(
    val shopId: String? = null,
    val foodName: String? = null,
    val foodPrice: String? = null,
    val foodImage: String? = null,
    var foodQuantity: Int? = null,
    var status: String? = null
)
