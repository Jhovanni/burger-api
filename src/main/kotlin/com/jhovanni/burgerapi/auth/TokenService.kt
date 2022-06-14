package com.jhovanni.burgerapi.auth

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class TokenService {
    @Value("security.jwt.secret")
    private lateinit var secret: String
    fun generate(subject: String, roles: List<String>): String {
        return Jwts
            .builder()
            .claim("authorities", roles)
            .setSubject(subject)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + 600000))
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact()
    }
}
