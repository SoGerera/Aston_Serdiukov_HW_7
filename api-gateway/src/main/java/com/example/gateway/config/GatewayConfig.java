package com.example.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // User Service Routes
                .route("user-service", r -> r
                        .path("/api/users/**")
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("userServiceCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/users"))
                                .retry(retryConfig -> retryConfig
                                        .setRetries(3)
                                        .setBackoff(null, null, null, false)))
                        .uri("lb://user-service"))

                // Swagger UI Route для User Service
                .route("user-service-swagger", r -> r
                        .path("/user-service/v3/api-docs/**", "/user-service/swagger-ui/**")
                        .filters(f -> f.rewritePath("/user-service/(?<segment>.*)", "/${segment}"))
                        .uri("lb://user-service"))

                .build();
    }
}
