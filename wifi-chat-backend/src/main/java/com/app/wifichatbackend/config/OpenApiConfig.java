package com.app.wifichatbackend.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI wifiChatOpenAPI() {
        return new OpenAPI()
                // ── API Metadata (shows at the top of Swagger UI) ──
                .info(new Info()
                        .title("WiFi Chat API")
                        .description("Real-time offline chat system over local WiFi. "
                                + "Supports JWT authentication, WebSocket messaging, "
                                + "chat room management, and message history.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Pranav")
                                .email("pranav@local.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))

                // ── JWT Authentication in Swagger ──
                // This adds the "Authorize 🔒" button in the Swagger UI
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .name("Bearer Authentication")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter your JWT token (without 'Bearer ' prefix)")
                        ));
    }
}
