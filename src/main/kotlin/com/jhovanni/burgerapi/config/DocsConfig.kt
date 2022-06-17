package com.jhovanni.burgerapi.config

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.context.annotation.Configuration

@Configuration
@SecurityScheme(name = "Bearer JWT", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
class DocsConfig {
}
