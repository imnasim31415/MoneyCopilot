package com.nasim.moneycopilot.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/** Configures Swagger UI with a JWT bearer token input. */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "MoneyCopilot API",
        version = "1.0",
        description = "AI-powered personal expense and tax tracker for Bangladesh"
    )
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
public class OpenApiConfig {
}
