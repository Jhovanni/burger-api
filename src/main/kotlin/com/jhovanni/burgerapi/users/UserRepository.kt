package com.jhovanni.burgerapi.users

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull

@Repository
class UserRepository(private val userJpaRepository: UserJpaRepository) {
    fun findUser(email: String): User? {
        return userJpaRepository.findOneByEmail(email).map { userJpa ->
            User(
                requireNotNull(userJpa.uuid),
                requireNotNull(userJpa.email),
                requireNotNull(userJpa.password),
                requireNotNull(userJpa.roles)
            )
        }.orElseGet { null }
    }

    fun exists(email: String): Boolean {
        return userJpaRepository.existsByEmail(email)
    }

    fun create(user: User): User {
        val userJpa = UserJpa()
        userJpa.uuid = user.uuid
        userJpa.email = user.email
        userJpa.password = user.password
        userJpa.roles = user.roles
        userJpaRepository.save(userJpa)
        return user
    }
}

interface UserJpaRepository : JpaRepository<UserJpa, UUID> {
    fun findOneByEmail(email: String): Optional<UserJpa>
    fun existsByEmail(email: String): Boolean
}

@Entity
@Table(name = "user_account")
open class UserJpa {
    @get:Id
    @get:NotNull
    open var uuid: UUID? = null

    @get:Column
    @get:NotNull
    open var email: String? = null

    @get:Column
    @get:NotNull
    open var password: String? = null

    @get:Column
    @get:ElementCollection
    open var roles: List<String>? = null

}
