package com.jhovanni.burgerapi.order

import com.jhovanni.burgerapi.auth.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/orders")
class OrderController(private val orderService: OrderService, private val authService: AuthService) {
    @Operation(
        summary = "Create order", security = [SecurityRequirement(name = "Bearer JWT")],
        description = "Requires authentication",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [(Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = OrderResponse::class)
                ))]
            ),
            ApiResponse(responseCode = "400", description = "Missing required fields", content = [Content()]),
            ApiResponse(responseCode = "401", description = "Missing authentication", content = [Content()]),
        ]
    )
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    fun create(@Valid @RequestBody request: CreateOrderRequest): OrderResponse {
        val userId = authService.getUserCredentials().id
        val orderItems = request.items.map { p -> OrderItem(p.productId, p.quantity) }
        val order = orderService.create(userId, request.client, orderItems)
        return OrderResponse(order)
    }

    @Operation(
        summary = "Get orders", security = [SecurityRequirement(name = "Bearer JWT")],
        description = "Requires authentication",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [(Content(
                    mediaType = "application/json",
                    array = ArraySchema(schema = Schema(implementation = OrderResponse::class))
                ))]
            ),
            ApiResponse(responseCode = "401", description = "Missing authentication", content = [Content()]),
        ]
    )
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    fun getAll(): OrdersResponse {
//        TODO("add pagination")
        return OrdersResponse(orderService.getAll())
    }

    @Operation(
        summary = "Get order", security = [SecurityRequirement(name = "Bearer JWT")],
        description = "Requires authentication",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [(Content(
                    mediaType = "application/json",
                    array = ArraySchema(schema = Schema(implementation = OrdersResponse::class))
                ))]
            ),
            ApiResponse(responseCode = "401", description = "Missing authentication", content = [Content()]),
            ApiResponse(responseCode = "404", description = "Not found", content = [Content()]),
        ]
    )
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    fun get(@PathVariable id: UUID): OrderResponse {
        return OrderResponse(orderService.get(id))
    }

    @Operation(
        summary = "Update product", security = [SecurityRequirement(name = "Bearer JWT")],
        description = "Requires an admin user. Allows to update only the fields provided.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [(Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = OrderResponse::class)
                ))]
            ),
            ApiResponse(responseCode = "400", description = "Missing required fields", content = [Content()]),
            ApiResponse(responseCode = "401", description = "Missing authentication", content = [Content()]),
            ApiResponse(
                responseCode = "403",
                description = "User not allowed to perform the operation",
                content = [Content()]
            ),
            ApiResponse(responseCode = "404", description = "Not found", content = [Content()]),
        ]
    )
    @PatchMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    fun update(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateOrderRequest
    ): OrderResponse {
        val orderItems = request.items?.map { p -> OrderItem(p.productId, p.quantity) }
        val order = orderService.update(id, request.client, orderItems, request.status)
        return OrderResponse(order)
    }

    @Operation(
        summary = "Delete product", security = [SecurityRequirement(name = "Bearer JWT")],
        description = "Requires an admin user.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [(Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = OrderResponse::class)
                ))]
            ),
            ApiResponse(responseCode = "401", description = "Missing authentication", content = [Content()]),
            ApiResponse(responseCode = "404", description = "Not found", content = [Content()]),
        ]
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    fun delete(@PathVariable id: UUID): OrderResponse {
        return OrderResponse(orderService.delete(id))
    }
}

data class CreateOrderRequest(val client: String, val items: List<OrderItemRequest>)
data class UpdateOrderRequest(val client: String?, val items: List<OrderItemRequest>?, val status: OrderStatus?)
data class OrderItemRequest(val productId: UUID, val quantity: Int)
data class OrderResponse(val order: Order)
data class OrdersResponse(val orders: List<Order>)
