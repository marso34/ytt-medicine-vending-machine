package com.example.ytt.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "약톡톡 REST API",
                description = "약톡톡 Spring Boot REST API 문서",
                version = "v0.0.1")
)
@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi vendingMachine() {
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
}
