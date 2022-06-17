package com.jhovanni.burgerapi.config

import com.jhovanni.burgerapi.auth.TokenService
import com.jhovanni.burgerapi.auth.UserCredentials
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.Serializable
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig {
    @Bean
    fun encoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun filterChain(http: HttpSecurity, jwtFilter: JwtFilter): SecurityFilterChain {
        http.csrf().disable()
            .authorizeRequests()
            .antMatchers(HttpMethod.POST, "/api/v1/auth").permitAll()
            .antMatchers(HttpMethod.GET, "/swagger-ui/**", "/v3/api-docs/**").permitAll()
            .anyRequest().authenticated()
            .and().exceptionHandling().authenticationEntryPoint(UnauthorizedEntryPoint())
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }
}

@Component
class UnauthorizedEntryPoint : AuthenticationEntryPoint, Serializable {
    override fun commence(
        request: HttpServletRequest?, response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
    }
}

@Component
class JwtFilter(private val tokenService: TokenService) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val credentials = findCredentials(request)
        if (credentials != null) {
            setCredentials(credentials, request)
        }
        filterChain.doFilter(request, response)
    }

    private fun findCredentials(request: HttpServletRequest): UserCredentials? {
        val authorizationHeader = request.getHeader("Authorization")
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null
        }
        val jwtToken = authorizationHeader.substring(7)
        return tokenService.extract(jwtToken)
    }

    private fun setCredentials(
        credentials: UserCredentials,
        request: HttpServletRequest
    ) {
        val authorities = credentials.roles.map(::SimpleGrantedAuthority)
        val usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(credentials, null, authorities)
        usernamePasswordAuthenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = usernamePasswordAuthenticationToken
    }
}
