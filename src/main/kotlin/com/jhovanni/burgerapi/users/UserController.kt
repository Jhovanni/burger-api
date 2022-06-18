package com.jhovanni.burgerapi.users

import com.jhovanni.burgerapi.auth.UserCredentials
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.security.Principal
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
    fun create(@Valid @RequestBody request: UserRequest): UserResponse {
        val user = userService.create(request.email, request.password, request.roles.orEmpty())
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
    fun getAll(): UsersResponse {
        return UsersResponse(userService.getAll())
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
    fun get(@PathVariable id: UUID): UserResponse {
        //TODO: id should be email as well, not only UUID
        return UserResponse(userService.get(id))
    }

    @Operation(
        summary = "Update user", security = [SecurityRequirement(name = "Bearer JWT")],
        description = "Requires an admin user or be the user owner. Only admin user can update modifying roles",
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
    fun update(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UserRequest, principal: Principal
    ): UserResponse {
        val credentials = getCredentials(principal)
        if (!credentials.roles.contains("ADMIN") && credentials.roles != request.roles.orEmpty()) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }

        val user = userService.update(id, request.email, request.password, request.roles.orEmpty())
        return UserResponse(user)
    }

    private fun getCredentials(principal: Principal): UserCredentials {
        if (principal is Authentication) {
            val innerPrincipal = principal.principal
            if (innerPrincipal is UserCredentials) {
                return innerPrincipal
            } else {
                throw ResponseStatusException(HttpStatus.FORBIDDEN)
            }
        } else {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }
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
    fun delete(@PathVariable id: UUID): UserResponse {
        val user = userService.delete(id)
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

