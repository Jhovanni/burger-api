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
        return userRepository.create(User(UUID.randomUUID(), email, passwordEncoder.encode(password), roles))
    }
}
