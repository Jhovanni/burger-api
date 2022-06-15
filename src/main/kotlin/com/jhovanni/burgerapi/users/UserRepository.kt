package com.jhovanni.burgerapi.users

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import javax.persistence.*

@Repository
class UserRepository(private val userJpaRepository: UserJpaRepository) {
    fun findUser(email: String): User? {
        val userJpa = userJpaRepository.findByIdOrNull(email)
        if (userJpa != null) {
            return User(userJpa.email!!, userJpa.password!!, userJpa.roles!!)
        }
        return null
    }
}

interface UserJpaRepository : JpaRepository<UserJpa, String>

@Entity
@Table(name = "user_account")
open class UserJpa {
    @get:Id
    open var email: String? = null

    @get:Column
    open var password: String? = null


    @get:ElementCollection
    open var roles: List<String>? = null
}
