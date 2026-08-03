package com.myapps.tasty_eats.models

data class AllMenu(
    val foodName: String? = null,
    val foodPrice: String? = null,
    val foodImage: String? = null,
    val foodDescription: String? = null,
    val foodIngredients: String? = null,
    var stocks: Boolean? = null
)
