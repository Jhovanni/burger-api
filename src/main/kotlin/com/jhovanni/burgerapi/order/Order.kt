package com.jhovanni.burgerapi.order

import java.util.*

data class Order(
    val id: UUID,
    val userId: UUID,
    val client: String,
    val items: List<OrderItem>,
    val status: OrderStatus,
    val created: Date,
    val processed: Date?
)

enum class OrderStatus(val id: Int) {
    UNKNOWN(0), PENDING(101), CANCELED(102), DELIVERED(103), DELIVERING(104);

    companion object {
        fun from(id: Int): OrderStatus {
            return OrderStatus.values().find { status -> status.id == id } ?: UNKNOWN
        }
    }

}

data class OrderItem(val productId: UUID, val quantity: Int)
