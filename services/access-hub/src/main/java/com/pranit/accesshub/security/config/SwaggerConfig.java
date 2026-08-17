package com.pranit.accesshub.security.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Identity & Management System.",
                description = "Generic Auth Service that can be used with any application.",
                contact = @Contact(
                        name = "Pranit Bhangale.",
                        url = "https://pranitbhangale.vercel.app",
                        email = "pranitbhangale1453@gmail.com"
                ),
                version = "1.0",
                summary = "This is an IAM Security module."
        )
)
public class SwaggerConfig {
}
