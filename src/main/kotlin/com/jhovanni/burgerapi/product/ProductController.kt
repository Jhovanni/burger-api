package com.jhovanni.burgerapi.product

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid

@Tag(name = "Product API")
@RestController
@RequestMapping("/api/v1/products")
class ProductController(private val productService: ProductService) {

    @Operation(
        summary = "Create product", security = [SecurityRequirement(name = "Bearer JWT")],
        description = "Requires an admin user",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "User created",
                content = [(Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ProductResponse::class)
                ))]
            ),
            ApiResponse(responseCode = "400", description = "Missing required fields", content = [Content()]),
            ApiResponse(responseCode = "401", description = "Missing authentication", content = [Content()]),
            ApiResponse(
                responseCode = "403",
                description = "User not allowed to perform the operation",
                content = [Content()]
            ),
        ]
    )
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    fun create(@Valid @RequestBody request: ProductRequest): ProductResponse {
        val product = productService.create(request.name, request.price, request.image, request.type)
        return ProductResponse(product)
    }

    @Operation(
        summary = "Get products", security = [SecurityRequirement(name = "Bearer JWT")],
        description = "Requires authentication",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [(Content(
                    mediaType = "application/json",
                    array = ArraySchema(schema = Schema(implementation = ProductsResponse::class))
                ))]
            ),
            ApiResponse(responseCode = "401", description = "Missing authentication", content = [Content()]),
        ]
    )
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    fun getAll(): ProductsResponse {
        return ProductsResponse(productService.getAll())
    }

    @Operation(
        summary = "Get product", security = [SecurityRequirement(name = "Bearer JWT")],
        description = "Requires authentication",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [(Content(
                    mediaType = "application/json",
                    array = ArraySchema(schema = Schema(implementation = ProductResponse::class))
                ))]
            ),
            ApiResponse(responseCode = "401", description = "Missing authentication", content = [Content()]),
            ApiResponse(responseCode = "404", description = "Product not found", content = [Content()]),
        ]
    )
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    fun get(@PathVariable id: UUID): ProductResponse {
        return ProductResponse(productService.get(id))
    }

    @Operation(
        summary = "Update product", security = [SecurityRequirement(name = "Bearer JWT")],
        description = "Requires an admin user",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [(Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ProductResponse::class)
                ))]
            ),
            ApiResponse(responseCode = "400", description = "Missing required fields", content = [Content()]),
            ApiResponse(responseCode = "401", description = "Missing authentication", content = [Content()]),
            ApiResponse(
                responseCode = "403",
                description = "User not allowed to perform the operation",
                content = [Content()]
            ),
            ApiResponse(responseCode = "404", description = "User does not exists", content = [Content()]),
        ]
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    fun update(
        @PathVariable id: UUID,
        @Valid @RequestBody request: ProductRequest
    ): ProductResponse {
        return ProductResponse(productService.update(id, request.name, request.price, request.image, request.type))
    }

    @Operation(
        summary = "Delete product", security = [SecurityRequirement(name = "Bearer JWT")],
        description = "Requires an admin user or be the user owner",
        responses = [
            ApiResponse(responseCode = "401", description = "Missing authentication", content = [Content()]),
            ApiResponse(
                responseCode = "403",
                description = "User not allowed to perform the operation",
                content = [Content()]
            ),
            ApiResponse(responseCode = "404", description = "Not found", content = [Content()]),
        ]
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    fun delete(@PathVariable id: UUID): ProductResponse {
        return ProductResponse(productService.delete(id))
    }
}

data class ProductRequest(val name: String, val price: Float, val image: String?, val type: String?)

data class ProductResponse(val product: Product)

data class ProductsResponse(val products: List<Product>)



