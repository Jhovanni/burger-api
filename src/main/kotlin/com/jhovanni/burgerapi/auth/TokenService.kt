package com.jhovanni.burgerapi.auth

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

private const val ROLES_KEY = "roles"
private const val TOKEN_LIFE_MS = 1800000
private const val ROLES_DELIMITER = ","

@Component
class TokenService {
    @Value("\${security.jwt.secret.key}")
    private lateinit var secretKey: String

    fun generateToken(userCredentials: UserCredentials): String {
        val issuedMs = System.currentTimeMillis()
        val expirationMs = issuedMs + TOKEN_LIFE_MS
        return Jwts
            .builder()
            .setSubject(userCredentials.id.toString())
            .claim(ROLES_KEY, userCredentials.roles.joinToString(ROLES_DELIMITER))
            .setIssuedAt(Date(issuedMs))
            .setExpiration(Date(expirationMs))
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .compact()
    }

    fun extractCredentials(jwtToken: String): UserCredentials? {
        return try {
            val claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken).body
            val roles =
                claims[ROLES_KEY].toString().split(ROLES_DELIMITER).toList().filter(String::isNotBlank)
            UserCredentials(UUID.fromString(claims.subject), roles)
        } catch (e: ExpiredJwtException) {
            null
        } catch (e: MalformedJwtException) {
            return null
        }
    }

}

