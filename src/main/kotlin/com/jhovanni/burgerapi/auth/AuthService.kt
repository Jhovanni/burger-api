package com.jhovanni.burgerapi.auth

import com.jhovanni.burgerapi.users.User
import com.jhovanni.burgerapi.users.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val tokenService: TokenService,
    private val passwordEncoder: PasswordEncoder
) {
    @Value("\${user.admin.uuid}")
    private lateinit var adminUuid: UUID

    @Value("\${user.admin.email}")
    private lateinit var adminEmail: String

    @Value("\${user.admin.password}")
    private lateinit var adminPassword: String

    fun authenticate(email: String, password: String): String {
        val user = getUser(email, password)
        val credentials = UserCredentials(user.id, user.roles)
        return tokenService.generateToken(credentials)
    }

    private fun getUser(email: String, password: String): User {
        if (email == adminEmail && password == adminPassword) {
            return User(adminUuid, email, listOf("ADMIN"))
        }

        val user = userRepository.findByEmail(email) ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        val encodedPassword = userRepository.findEncodedPasswordById(user.id)
        if (!passwordEncoder.matches(password, encodedPassword)) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        }
        return user
    }

    fun getUserCredentials(): UserCredentials {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication is UsernamePasswordAuthenticationToken) {
            val principal = authentication.principal
            if (principal is UserCredentials) {
                return principal
            }
        }
        throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
    }
}
