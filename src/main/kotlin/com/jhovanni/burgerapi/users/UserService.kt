package com.jhovanni.burgerapi.users

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class UserService(private val userRepository: UserRepository, private val passwordEncoder: PasswordEncoder) {
    @Value("\${user.admin.email}")
    private lateinit var adminEmail: String

    fun create(email: String, password: String, roles: List<String>): User {
        if (email == adminEmail || userRepository.existsByEmail(email)) {
            throw ResponseStatusException(HttpStatus.CONFLICT)
        }
        return userRepository.save(User(UUID.randomUUID(), email, roles), passwordEncoder.encode(password))
    }

    fun update(id: UUID, email: String, password: String, roles: List<String>): User {
        if (email == adminEmail) {
            throw ResponseStatusException(HttpStatus.CONFLICT)
        }
        val user = userRepository.findById(id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        if (email != user.email && userRepository.existsByEmail(email)) {
            throw ResponseStatusException(HttpStatus.CONFLICT)
        }
        return userRepository.save(User(id, email, roles), passwordEncoder.encode(password))
    }

    fun getAll(): List<User> {
        return userRepository.getAll()
    }

    fun get(id: UUID): User {
        return userRepository.findById(id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

    fun delete(id: UUID): User {
        val user = userRepository.findById(id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        userRepository.delete(id)
        return user
    }
}
