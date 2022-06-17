package com.jhovanni.burgerapi.auth

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

private const val ROLES_KEY = "roles"
private const val TOKEN_LIFE_MS = 600000
private const val ROLES_DELIMITER = ","

@Component
class TokenService {
    @Value("\${security.jwt.secret.key}")
    private lateinit var secretKey: String

    fun generate(userCredentials: UserCredentials): String {
        val issuedMs = System.currentTimeMillis()
        val expirationMs = issuedMs + TOKEN_LIFE_MS
        return Jwts
            .builder()
            .setSubject(userCredentials.subject)
            .claim(ROLES_KEY, userCredentials.roles.joinToString(ROLES_DELIMITER))
            .setIssuedAt(Date(issuedMs))
            .setExpiration(Date(expirationMs))
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .compact()
    }

    fun extract(jwtToken: String): UserCredentials? {
        return try {
            val claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken).body
            val roles =
                claims[ROLES_KEY].toString().split(ROLES_DELIMITER).toList().filter(String::isNotBlank)
            UserCredentials(claims.subject, roles)
        } catch (e: ExpiredJwtException) {
            null
        }
    }

}

data class UserCredentials(val subject: String, val roles: List<String>)
