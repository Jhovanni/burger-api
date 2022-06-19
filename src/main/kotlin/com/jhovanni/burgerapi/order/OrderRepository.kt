package com.jhovanni.burgerapi.order

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull

@Repository
class OrderRepository(private val orderJpaRepository: OrderJpaRepository) {
    private val mapper = jacksonObjectMapper()
    fun save(order: Order): Order {
        orderJpaRepository.save(toOrderJpa(order))
        return order
    }

    fun getAll(): List<Order> = orderJpaRepository.findAll().map(::toOrder)

    fun getAll(page: Int, size: Int): List<Order> =
        orderJpaRepository.findAll(PageRequest.of(page, size)).get().map(::toOrder).toList()

    fun findById(id: UUID): Order? = orderJpaRepository.findById(id).map(::toOrder).orElseGet { null }

    fun delete(id: UUID) {
        orderJpaRepository.deleteById(id)
    }

    private fun toOrderJpa(order: Order): OrderJpa {
        val orderJpa = OrderJpa()
        orderJpa.id = order.id
        orderJpa.userId = order.userId
        orderJpa.client = order.client
        orderJpa.items = mapper.writeValueAsString(order.items)
        orderJpa.status = order.status.id
        orderJpa.created = order.created
        orderJpa.processed = order.processed
        return orderJpa
    }

    private fun toOrder(orderJpa: OrderJpa): Order {
        val itemsString = requireNotNull(orderJpa.items)
        val items: List<OrderItem> = try {
            mapper.readValue(itemsString, Array<OrderItem>::class.java).asList()
        } catch (e: Exception) {
            emptyList()
        }

        return Order(
            requireNotNull(orderJpa.id),
            requireNotNull(orderJpa.userId),
            requireNotNull(orderJpa.client),
            items,
            OrderStatus.from(requireNotNull(orderJpa.status)),
            requireNotNull(orderJpa.created),
            orderJpa.processed
        )
    }

}

interface OrderJpaRepository : JpaRepository<OrderJpa, UUID>


@Entity
@Table(name = "order_element")
open class OrderJpa {
    @get:Id
    @get:NotNull
    open var id: UUID? = null

    @get:Column
    @get:NotNull
    open var userId: UUID? = null

    @get:Column
    @get:NotNull
    open var client: String? = null

    @get:Column
    @get:NotNull
    open var items: String? = null

    @get:Column
    @get:NotNull
    open var status: Int? = null

    @get:Column
    @get:NotNull
    open var created: Date? = null

    @get:Column
    open var processed: Date? = null
}
