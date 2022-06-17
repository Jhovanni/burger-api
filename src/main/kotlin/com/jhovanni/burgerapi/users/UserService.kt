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
    fun createUser(email: String, password: String, roles: List<String>): User {
        if (email == adminEmail || userRepository.exists(email)) {
            throw ResponseStatusException(HttpStatus.CONFLICT)
        }
        return userRepository.save(User(UUID.randomUUID(), email, roles), passwordEncoder.encode(password))
    }

    fun updateUser(id: UUID, email: String, password: String, roles: List<String>): User {
        if (email == adminEmail) {
            throw ResponseStatusException(HttpStatus.CONFLICT)
        }
        val user = userRepository.findUser(id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        if (email != user.email && userRepository.exists(email)) {
            throw ResponseStatusException(HttpStatus.CONFLICT)
        }
        return userRepository.save(User(id, email, roles), passwordEncoder.encode(password))
    }

    fun getUsers(): List<User> {
        return userRepository.getAll()
    }

    fun getUser(id: UUID): User {
        return userRepository.findUser(id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

    fun deleteUser(id: UUID): User {
        return userRepository.delete(id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }
}
