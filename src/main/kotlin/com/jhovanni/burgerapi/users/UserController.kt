package com.jhovanni.burgerapi.users

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@RestController
@RequestMapping("/api/v1/users")
class UserController(private val userService: UserService) {
    @Operation(summary = "Get all users")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Success",
            content = [(Content(
                mediaType = "application/json",
                array = ArraySchema(schema = Schema(implementation = CreateUserResponse::class))
            ))]
        ),
        ApiResponse(responseCode = "401", description = "Unauthenticated", content = [Content()]),
        ApiResponse(responseCode = "403", description = "Forbidden", content = [Content()]),
    )
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    fun getUsers(): GetUsersResponse {
        return GetUsersResponse(userService.getUsers())
    }

    @Operation(summary = "Get a single user")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Success",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = CreateUserResponse::class)
            ))]
        ),
        ApiResponse(responseCode = "401", description = "Unauthenticated", content = [Content()]),
        ApiResponse(responseCode = "403", description = "Forbidden", content = [Content()]),
        ApiResponse(responseCode = "404", description = "User not found", content = [Content()]),
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    fun getUser(@PathVariable id: UUID): GetUserResponse {
        return GetUserResponse(userService.getUser(id))
    }

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
        ApiResponse(responseCode = "401", description = "Unauthenticated", content = [Content()]),
        ApiResponse(responseCode = "403", description = "Forbidden", content = [Content()]),
        ApiResponse(
            responseCode = "409",
            description = "User with the given email already exists",
            content = [Content()]
        )
    )
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    fun createUSer(@Valid @RequestBody request: CreateUserRequest): CreateUserResponse {
        val user = userService.createUser(request.email, request.password, request.roles.orEmpty())
        return CreateUserResponse(user)
    }

    @Operation(summary = "Updates an user")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "User data updated",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = CreateUserResponse::class)
            ))]
        ),
        ApiResponse(responseCode = "400", description = "Missing required fields", content = [Content()]),
        ApiResponse(responseCode = "401", description = "Unauthenticated", content = [Content()]),
        ApiResponse(responseCode = "403", description = "Forbidden", content = [Content()]),
        ApiResponse(responseCode = "404", description = "User does not exists", content = [Content()]),
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    fun updateUser(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateUserRequest
    ): UpdateUserResponse {
        val user = userService.updateUser(id, request.email, request.password, request.roles.orEmpty())
        return UpdateUserResponse(user)
    }

    @Operation(summary = "Deletes an user")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Success",
            content = [(Content(
                mediaType = "application/json",
                schema = Schema(implementation = DeleteUserResponse::class)
            ))]
        ),
        ApiResponse(responseCode = "400", description = "Missing required fields", content = [Content()]),
        ApiResponse(responseCode = "401", description = "Unauthenticated", content = [Content()]),
        ApiResponse(responseCode = "403", description = "Forbidden", content = [Content()]),
        ApiResponse(responseCode = "404", description = "User does not exists", content = [Content()]),
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    fun deleteUser(@PathVariable id: UUID): UpdateUserResponse {
        val user = userService.deleteUser(id)
        return UpdateUserResponse(user)
    }
}

data class CreateUserRequest(
    @field:NotBlank val email: String,
    @field:NotBlank val password: String,
    val roles: List<String>?
)

data class CreateUserResponse(val user: User)
data class GetUsersResponse(val users: List<User>)
data class GetUserResponse(val user: User)

data class UpdateUserRequest(
    @field:NotBlank val email: String,
    @field:NotBlank val password: String,
    val roles: List<String>?
)

data class UpdateUserResponse(val user: User)
data class DeleteUserResponse(val user: User)

