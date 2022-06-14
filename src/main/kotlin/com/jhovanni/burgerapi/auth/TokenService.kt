package com.jhovanni.burgerapi.auth

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Component
import java.util.*

@Component
class TokenService {
    fun generate(subject: String, roles: List<String>): String {
        val secretKey = "javainuse"
        return Jwts
            .builder()
            .claim("authorities", roles)
            .setSubject(subject)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + 600000))
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .compact()
    }
}
