package com.jhovanni.burgerapi.product

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Repository
class ProductRepository(private val productJpaRepository: ProductJpaRepository) {

    fun save(product: Product): Product {
        val productJpa = toProductJpa(product)
        productJpaRepository.save(productJpa)
        return product
    }

    fun findAll(): List<Product> = productJpaRepository.findAll().map(::toProduct)

    fun find(id: UUID): Product? = productJpaRepository.findById(id).map(::toProduct).orElseGet { null }

    fun delete(id: UUID) = productJpaRepository.deleteById(id)

    private fun toProductJpa(product: Product): ProductJpa {
        val productJpa = ProductJpa()
        productJpa.id = product.id
        productJpa.name = product.name
        productJpa.description = product.description
        productJpa.price = product.price
        productJpa.image = product.image
        productJpa.type = product.type
        productJpa.category = product.category
        productJpa.created = product.created
        return productJpa
    }

    private fun toProduct(productJpa: ProductJpa) = Product(
        requireNotNull(productJpa.id),
        requireNotNull(productJpa.name),
        productJpa.description,
        requireNotNull(productJpa.price),
        productJpa.image,
        productJpa.type,
        productJpa.category,
        requireNotNull(productJpa.created)
    )
}

interface ProductJpaRepository : JpaRepository<ProductJpa, UUID>

@Entity
@Table(name = "product")
open class ProductJpa {
    @get:Id
    @get:NotNull
    open var id: UUID? = null

    @get:Column
    @get:NotNull
    open var name: String? = null

    @get:Column
    open var description: String? = null

    @get:Column
    @get:NotNull
    open var price: Float? = null

    @get:Column
    open var image: String? = null

    @get:Column
    open var type: String? = null

    @get:Column
    open var category: String? = null

    @get:Column
    @get:NotNull
    open var created: Date? = null


}
