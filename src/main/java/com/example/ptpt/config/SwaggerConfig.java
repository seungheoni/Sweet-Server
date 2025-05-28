package com.example.ptpt.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;


@Configuration
public class SwaggerConfig {

    @Value("${springdoc..server-url}")
    private String serverUrl;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .servers(Collections.singletonList(
                        new Server().url(serverUrl)
                ))
                .info(new Info()
                        .title("PTPT API")
                        .description("PTPT 서비스 API 명세서")
                        .version("1.0.0"));
    }
}
