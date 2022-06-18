package com.jhovanni.burgerapi.product

import java.util.*

data class Product(
    val id: UUID,
    val name: String,
    val price: Float,
    val image: String?,
    val type: String?,
    val created: Date
)

enum class ProductType {

}
