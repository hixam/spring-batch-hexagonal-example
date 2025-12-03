package com.example.batchExample.infrastructure.config;

import io.github.resilience4j.retry.RetryRegistry;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
public class Resilience4jDebugConfig {

    private final RetryRegistry retryRegistry;

    public Resilience4jDebugConfig(RetryRegistry retryRegistry) {
        this.retryRegistry = retryRegistry;
    }

    @PostConstruct
    public void debugResilience4j() {
        log.info("=== RESILIENCE4J DEBUG ===");

        // Verificar retrys configurados
        retryRegistry.getAllRetries().forEach(retry -> {
            log.info("Retry encontrado: {}", retry.getName());
            log.info("  Config: {}", retry.getRetryConfig());
            log.info("  EventPublisher activo: {}", retry.getEventPublisher());

            // Registrar listener para debug
            retry.getEventPublisher()
                    .onRetry(event -> log.info("🔄 RETRY event fired: {}", event))
                    .onSuccess(event -> log.info("✅ SUCCESS event fired: {}", event))
                    .onError(event -> log.info("❌ ERROR event fired: {}", event));
        });

        log.info("=== FIN DEBUG ===");
    }
}