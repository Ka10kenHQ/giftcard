package com.gitfcard.giftcard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    @Bean
    protected OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                    .title("GiftCard Shop API")
                    .description("API documentation for the GiftCard shop project")
                    .version("v1.0")
                )
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                    .addSecuritySchemes("basicAuth", 
                        new SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("basic")
                    )
                );
    }
}

