package com.github.ljl1leina.sololog.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        // 安全方案的"定义"：告诉 Swagger 有一种认证方式叫 bearerAuth，
        // 类型是 HTTP Bearer（即 Authorization: Bearer xxx 请求头），格式是 JWT
        SecurityScheme scheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("bearerAuth", scheme))
                // 全局声明：整个 API 都可能用到这个安全方案
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
