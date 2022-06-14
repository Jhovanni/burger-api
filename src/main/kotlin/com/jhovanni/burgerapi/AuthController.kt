package com.jhovanni.burgerapi

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/auth")
class AuthController {

    @PostMapping
    fun postAuth(@RequestBody request: AuthRequest): AuthResponse {
        return AuthResponse("not a real token")
    }
}

data class AuthRequest(val email: String, val password: String)

data class AuthResponse(val token: String)
