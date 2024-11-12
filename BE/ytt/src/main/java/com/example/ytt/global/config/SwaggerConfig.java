package com.example.ytt.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "약톡톡 REST API",
                description = "약톡톡 Spring Boot REST API 문서",
                version = "v0.0.1"),
        security = @SecurityRequirement(name = "Authorization")
)
@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME_ACCESS = "Authorization";
    private static final String SECURITY_SCHEME_NAME_REFRESH = "refresh";

    // JWT 인증
    @Bean
    public OpenAPI swaggerApi() {
        Components components = new Components()
                .addSecuritySchemes(SECURITY_SCHEME_NAME_ACCESS, new SecurityScheme()
                        .name(SECURITY_SCHEME_NAME_ACCESS)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .in(SecurityScheme.In.HEADER)
                        .description("JWT 토큰 정보"));
                // refresh 인증 추가
//                .addSecuritySchemes(SECURITY_SCHEME_NAME_REFRESH, new SecurityScheme()
//                        .name(SECURITY_SCHEME_NAME_REFRESH)
//                        .type(SecurityScheme.Type.APIKEY)
//                        .in(SecurityScheme.In.HEADER)
//                        .description("refresh 토큰 정보"))

        return new OpenAPI().components(components);
    }

    @Bean
    public GroupedOpenApi userGroup() {
        return GroupedOpenApi.builder()
                .group("User")     // 그룹 이름
                .pathsToMatch("/user/**")  // 그룹에 속하는 경로
                .build();
    }
    @Bean
    public GroupedOpenApi authGroup() {
        return GroupedOpenApi.builder()
                .group("Auth")     // 그룹 이름
                .pathsToMatch("/auth/**")  // 그룹에 속하는 경로
                .build();
    }

    @Bean
    public GroupedOpenApi orderGroup() {
        return GroupedOpenApi.builder()
                .group("Order")     // 그룹 이름
                .pathsToMatch("/orders/**")  // 그룹에 속하는 경로
                .build();
    }

    @Bean
    public GroupedOpenApi machineGroup() {
        return GroupedOpenApi.builder()
                .group("vending-machine")     // 그룹 이름
                .pathsToMatch("/vending-machine/**")  // 그룹에 속하는 경로
                .build();
    }

    @Bean
    public GroupedOpenApi medicineGroup() {
        return GroupedOpenApi.builder()
                .group("medicine")     // 그룹 이름
                .pathsToMatch("/medicine/**")  // 그룹에 속하는 경로
                .build();
    }

    @Bean
    public GroupedOpenApi inboundGroup() {
        return GroupedOpenApi.builder()
                .group("inbound")     // 그룹 이름
                .pathsToMatch("/inbound/**")  // 그룹에 속하는 경로
                .build();
    }
}
