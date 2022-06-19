package com.jhovanni.burgerapi.users

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull

@Repository
class UserRepository(private val userJpaRepository: UserJpaRepository) {
    private val mapper = jacksonObjectMapper()
    fun findById(id: UUID): User? = userJpaRepository.findById(id).map(::toUser).orElseGet { null }

    fun findByEmail(email: String): User? = userJpaRepository.findOneByEmail(email).map(::toUser).orElseGet { null }

    fun existsByEmail(email: String): Boolean = userJpaRepository.existsByEmail(email)

    fun save(user: User, encodedPassword: String): User {
        val userJpa = toUserJpa(user, encodedPassword)
        userJpaRepository.save(userJpa)
        return user
    }

    fun getAll(): List<User> = userJpaRepository.findAll().map(::toUser)

    fun delete(id: UUID) = userJpaRepository.deleteById(id)

    fun findEncodedPasswordById(id: UUID): String? = userJpaRepository.getEncodedPasswordById(id)

    private fun toUser(userJpa: UserJpa): User {
        val rolesString = requireNotNull(userJpa.roles)
        val roles: List<String> = try {
            mapper.readValue(rolesString, Array<String>::class.java).asList()
        } catch (e: Exception) {
            emptyList()
        }

        return User(
            requireNotNull(userJpa.id),
            requireNotNull(userJpa.email),
            roles
        )
    }

    private fun toUserJpa(
        user: User,
        encodedPassword: String
    ): UserJpa {
        val userJpa = UserJpa()
        userJpa.id = user.id
        userJpa.email = user.email
        userJpa.encodedPassword = encodedPassword
        userJpa.roles = mapper.writeValueAsString(user.roles)
        return userJpa
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
    @get:NotNull
    open var roles: String? = null

}
