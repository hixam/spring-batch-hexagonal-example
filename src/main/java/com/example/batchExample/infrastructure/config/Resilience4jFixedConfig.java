package com.example.batchExample.infrastructure.config;

import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.time.Duration;

@Slf4j
@Configuration
public class Resilience4jFixedConfig {

    @Bean
    @Primary  // ← ¡IMPORTANTE!
    public RetryRegistry retryRegistry(ShouldRetryPredicate shouldRetryPredicate) {
        log.info("🔧 Configurando Resilience4j 2.3.0 correctamente");

        // MÉTODO 1: Exponential Backoff con IntervalFunction (CORRECTO para 2.3.0)
        RetryConfig externalApiConfig = RetryConfig.custom()
                .maxAttempts(5)
                .intervalFunction(IntervalFunction.ofExponentialBackoff(
                        Duration.ofSeconds(5).toMillis(), // initialIntervalMillis
                        2.0,                              // multiplier
                        Duration.ofSeconds(10).toMillis() // maxIntervalMillis
                ))
                .retryOnResult(shouldRetryPredicate)
                .retryExceptions(Exception.class)
                .ignoreExceptions()
                .failAfterMaxAttempts(false)
                .build();

        // MÉTODO 2: Alternativa usando waitDuration con exponential backoff
        RetryConfig alternativeConfig = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofSeconds(5))
                // En 2.3.0, exponential backoff se configura así:
                .intervalFunction(IntervalFunction.ofExponentialRandomBackoff(
                        5000L,    // initialDelayMillis
                        2.0,      // multiplier
                        0.5       // randomizationFactor
                ))
                .retryOnResult(shouldRetryPredicate)
                .build();

        RetryRegistry registry = RetryRegistry.of(externalApiConfig);
        registry.retry("externalApiRetry", externalApiConfig);

        log.info("✅ Registry creado con configuración 2.3.0");
        return registry;
    }
}