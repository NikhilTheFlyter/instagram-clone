package com.instagram.follow.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Follow Service API")
                .description("Follow service for Instagram Clone - handles follow/unfollow operations, follower lists, and follow statistics")
                .version("1.0.0")
                .contact(new Contact().name("Instagram Clone").email("admin@instagram-clone.com")));
    }
}
