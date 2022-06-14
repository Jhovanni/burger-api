package com.jhovanni.burgerapi

import com.jhovanni.burgerapi.auth.TokenService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/auth")
class AuthController(private val tokenService: TokenService) {

    @PostMapping
    fun postAuth(@RequestBody request: AuthRequest): AuthResponse {
        val token = tokenService.generate(request.email, listOf("ADMIN"))
        return AuthResponse(token)
    }
}

data class AuthRequest(val email: String, val password: String)
data class AuthResponse(val token: String)
