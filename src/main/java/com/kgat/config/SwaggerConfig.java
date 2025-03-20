package com.kgat.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Collab API", version = "1.0", description = "사내 협업 시스템 API 명세서"))
public class SwaggerConfig {
}