package com.jhovanni.burgerapi

import com.jhovanni.burgerapi.auth.TokenService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(private val tokenService: TokenService) {

    @PostMapping
    fun postAuth(@Valid @RequestBody request: AuthRequest): AuthResponse {
        val token = tokenService.generate(request.email, listOf("ADMIN"))
        return AuthResponse(token)
    }
}

data class AuthRequest(@field:NotBlank val email: String, @field:NotBlank val password: String)
data class AuthResponse(val token: String)
