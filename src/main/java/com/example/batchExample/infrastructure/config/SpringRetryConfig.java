package com.example.batchExample.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;

@Configuration
public class SpringRetryConfig {

    @Bean
    public RetryListener myRetryListener() {
        return new RetryListener() {
            @Override
            public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
                System.out.println("Reintento iniciado");
                return true;
            }

            @Override
            public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
                if (throwable != null) {
                    System.out.println("El reintento falló después de " + context.getRetryCount() + " intentos.");
                } else {
                    System.out.println("Reintento exitoso");
                }            }

            @Override
            public <T, E extends Throwable> void onSuccess(RetryContext context, RetryCallback<T, E> callback, T result) {
                System.out.println("{onSuccess()} - Reintento exitoso");
            }

            @Override
            public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
                System.out.println("Error durante el reintento, intentando de nuevo...");
            }
        };
    }
}
