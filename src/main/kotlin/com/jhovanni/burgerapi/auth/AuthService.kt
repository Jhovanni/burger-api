package com.jhovanni.burgerapi.auth

import com.jhovanni.burgerapi.users.User
import com.jhovanni.burgerapi.users.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

private const val s = "admin@admin.com"

@Service
class AuthService(private val userRepository: UserRepository, private val tokenService: TokenService) {
    @Value("\${user.admin.uuid}")
    private lateinit var adminUuid: UUID

    @Value("\${user.admin.email}")
    private lateinit var adminEmail: String

    @Value("\${user.admin.password}")
    private lateinit var adminPassword: String

    fun authenticate(email: String, password: String): String {
        val user = getUser(email, password)
        return tokenService.generate(user.email, user.roles)
    }

    private fun getUser(email: String, password: String): User {
        if (email == adminEmail && password == adminPassword) {
            return User(adminUuid, email, password, listOf("ADMIN"))
        }

        val user = userRepository.findUser(email) ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        if (user.password != password) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        }
        return user
    }
}
