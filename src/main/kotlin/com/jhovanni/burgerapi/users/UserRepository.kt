package com.jhovanni.burgerapi.users

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull

@Repository
class UserRepository(private val userJpaRepository: UserJpaRepository) {
    fun findUser(id: UUID): User? {
        return userJpaRepository.findById(id).map(::mapToUser).orElseGet { null }
    }

    fun findUser(email: String): User? {
        return userJpaRepository.findOneByEmail(email).map(::mapToUser).orElseGet { null }
    }

    private fun mapToUser(userJpa: UserJpa) = User(
        requireNotNull(userJpa.id),
        requireNotNull(userJpa.email),
        requireNotNull(userJpa.roles)
    )

    fun exists(email: String): Boolean {
        return userJpaRepository.existsByEmail(email)
    }

    fun exists(id: UUID): Boolean {
        return userJpaRepository.existsById(id)
    }

    fun save(user: User, encodedPassword: String): User {
        val userJpa = UserJpa()
        userJpa.id = user.id
        userJpa.email = user.email
        userJpa.encodedPassword = encodedPassword
        userJpa.roles = user.roles
        userJpaRepository.save(userJpa)
        return user
    }

    fun getAll(): List<User> {
        return userJpaRepository.findAll().map(::mapToUser)
    }

    fun delete(id: UUID): User? {
        val userJpa = userJpaRepository.findByIdOrNull(id) ?: return null
        userJpaRepository.delete(userJpa)
        return mapToUser(userJpa)
    }

    fun getEncodedPassword(id: UUID): String? {
        return userJpaRepository.getEncodedPasswordById(id)
    }
}

interface UserJpaRepository : JpaRepository<UserJpa, UUID> {
    fun findOneByEmail(email: String): Optional<UserJpa>
    fun existsByEmail(email: String): Boolean

    @org.springframework.data.jpa.repository.Query("SELECT encodedPassword FROM UserJpa WHERE  id = :id")
    fun getEncodedPasswordById(id: UUID): String?
}

@Entity
@Table(name = "user_account")
open class UserJpa {
    @get:Id
    @get:NotNull
    open var id: UUID? = null

    @get:Column
    @get:NotNull
    open var email: String? = null

    @get:Column
    @get:NotNull
    open var encodedPassword: String? = null

    @get:Column
    @get:ElementCollection
    open var roles: List<String>? = null

}
