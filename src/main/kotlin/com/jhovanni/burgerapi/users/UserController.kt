package com.jhovanni.burgerapi.users

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@RestController
@RequestMapping("/api/v1/users")
class UserController(private val userService: UserService) {
    @Operation(
        summary = "Create user", security = [SecurityRequirement(name = "Bearer JWT")],
        description = "Requires an admin user",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "User created",
                content = [(Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = UserResponse::class)
                ))]
            ),
            ApiResponse(responseCode = "400", description = "Missing required fields", content = [Content()]),
            ApiResponse(responseCode = "401", description = "Missing authentication", content = [Content()]),
            ApiResponse(
                responseCode = "403",
                description = "User not allowed to perform the operation",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "409",
                description = "User with the given email already exists",
                content = [Content()]
            )
        ]
    )
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    fun createUSer(@Valid @RequestBody request: UserRequest): UserResponse {
        val user = userService.createUser(request.email, request.password, request.roles.orEmpty())
        return UserResponse(user)
    }

    @Operation(
        summary = "Get users", security = [SecurityRequirement(name = "Bearer JWT")],
        description = "Requires an admin user",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [(Content(
                    mediaType = "application/json",
                    array = ArraySchema(schema = Schema(implementation = UsersResponse::class))
                ))]
            ),
            ApiResponse(responseCode = "401", description = "Missing authentication", content = [Content()]),
            ApiResponse(
                responseCode = "403",
                description = "User not allowed to perform the operation",
                content = [Content()]
            )
        ]
    )
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    fun getUsers(): UsersResponse {
        return UsersResponse(userService.getUsers())
    }

    @Operation(
        summary = "Get user", security = [SecurityRequirement(name = "Bearer JWT")],
        description = "Requires an admin user or be the user owner",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [(Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = UserResponse::class)
                ))]
            ),
            ApiResponse(responseCode = "401", description = "Missing authentication", content = [Content()]),
            ApiResponse(
                responseCode = "403",
                description = "User not allowed to perform the operation",
                content = [Content()]
            ),
            ApiResponse(responseCode = "404", description = "User not found", content = [Content()]),
        ]
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') || #id == authentication.principal.id")
    fun getUser(@PathVariable id: UUID): UserResponse {
        return UserResponse(userService.getUser(id))
    }

    @Operation(
        summary = "Update user", security = [SecurityRequirement(name = "Bearer JWT")],
        description = "Requires an admin user or be the user owner",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "User data updated",
                content = [(Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = UserResponse::class)
                ))]
            ),
            ApiResponse(responseCode = "400", description = "Missing required fields", content = [Content()]),
            ApiResponse(responseCode = "401", description = "Missing authentication", content = [Content()]),
            ApiResponse(
                responseCode = "403",
                description = "User not allowed to perform the operation",
                content = [Content()]
            ),
            ApiResponse(responseCode = "404", description = "User does not exists", content = [Content()]),
        ]
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') || #id == authentication.principal.id")
    fun updateUser(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UserRequest
    ): UserResponse {
        val user = userService.updateUser(id, request.email, request.password, request.roles.orEmpty())
        return UserResponse(user)
    }

    @Operation(
        summary = "Delete user", security = [SecurityRequirement(name = "Bearer JWT")],
        description = "Requires an admin user or be the user owner",
        responses = [
            ApiResponse(responseCode = "401", description = "Missing authentication", content = [Content()]),
            ApiResponse(
                responseCode = "403",
                description = "User not allowed to perform the operation",
                content = [Content()]
            ),
            ApiResponse(responseCode = "404", description = "User does not exists", content = [Content()]),
        ]
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') || #id == authentication.principal.id")
    fun deleteUser(@PathVariable id: UUID): UserResponse {
        val user = userService.deleteUser(id)
        return UserResponse(user)
    }
}

data class UserRequest(
    @field:NotBlank val email: String,
    @field:NotBlank val password: String,
    val roles: List<String>?
)

data class UserResponse(val user: User)
data class UsersResponse(val users: List<User>)

