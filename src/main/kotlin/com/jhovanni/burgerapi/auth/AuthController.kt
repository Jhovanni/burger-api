package com.jhovanni.burgerapi.auth

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(private val authService: AuthService) {

    @Operation(summary = "Creates authentication token")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Success authentication",
            content = [(Content(mediaType = "application/json", schema = Schema(implementation = AuthResponse::class)))]
        ),
        ApiResponse(responseCode = "400", description = "Missing required fields", content = [Content()]),
        ApiResponse(responseCode = "401", description = "Unauthorized", content = [Content()])
    )
    @PostMapping
    fun postAuth(@Valid @RequestBody request: AuthRequest): AuthResponse {
        val token = authService.authenticate(request.email, request.password)
        return AuthResponse(token)
    }
}

data class AuthRequest(@field:NotBlank val email: String, @field:NotBlank val password: String)
data class AuthResponse(val token: String)
