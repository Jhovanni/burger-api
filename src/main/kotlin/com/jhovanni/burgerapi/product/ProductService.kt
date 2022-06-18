package com.jhovanni.burgerapi.product

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class ProductService(private val productRepository: ProductRepository) {
    fun create(name: String, price: Float, image: String?, type: String?): Product {
        val product = Product(id = UUID.randomUUID(), name, price, image, type, created = Date())
        return productRepository.save(product)
    }

    fun getAll(): List<Product> {
        return productRepository.findAll()
    }

    fun get(id: UUID): Product {
        return productRepository.find(id) ?: throw throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

    fun update(id: UUID, name: String, price: Float, image: String?, type: String?): Product {
        val existing = get(id)
        val product = Product(id, name, price, image, type, existing.created)
        return productRepository.save(product)
    }

    fun delete(id: UUID): Product {
        val product = productRepository.find(id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        productRepository.delete(id)
        return product
    }

}
