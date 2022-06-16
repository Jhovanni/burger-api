package com.jhovanni.burgerapi.users

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@RestController
@RequestMapping("/api/v1/users")
class UserController(private val userService: UserService) {
    @Operation(summary = "Creates a new user")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "User created",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = CreateUserResponse::class)
            ))]
        ),
        ApiResponse(responseCode = "400", description = "Missing required fields", content = [Content()]),
        ApiResponse(responseCode = "403", description = "Forbidden", content = [Content()]),
        ApiResponse(
            responseCode = "409",
            description = "User with the given email already exists",
            content = [Content()]
        )
    )
    @PutMapping
    fun createUSer(@Valid @RequestBody request: CreateUserRequest): CreateUserResponse {
        val user = userService.createUser(request.email, request.password, request.roles.orEmpty())
        return CreateUserResponse(user.uuid, user.email, user.roles)
    }
}

data class CreateUserRequest(
    @field:NotBlank val email: String,
    @field:NotBlank val password: String,
    val roles: List<String>?
)

data class CreateUserResponse(val userUuid: UUID, val email: String, val roles: List<String>)

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
