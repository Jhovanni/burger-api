package com.jhovanni.burgerapi.order

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class OrderService(private val orderRepository: OrderRepository) {
    fun create(userId: UUID, client: String, orderItems: List<OrderItem>): Order {
        val order = Order(UUID.randomUUID(), userId, client, orderItems, OrderStatus.PENDING, Date(), null)
        return orderRepository.save(order)
    }

    fun getAll(): List<Order> {
        return orderRepository.getAll()
    }

    fun get(id: UUID): Order {
        return orderRepository.findById(id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

    fun update(id: UUID, client: String?, items: List<OrderItem>?, status: OrderStatus?): Order {
        val existing = get(id)

        var processed: Date? = null
        if (existing.processed != null) {
            processed = existing.processed
        } else if (status == OrderStatus.DELIVERED) {
            processed = Date()
        }

        val order = Order(
            id,
            existing.userId,
            client ?: existing.client,
            items ?: existing.items,
            status ?: existing.status,
            existing.created,
            processed
        )
        return orderRepository.save(order)
    }

    fun delete(id: UUID): Order {
        val order = get(id)
        orderRepository.delete(id)
        return order
    }
}
