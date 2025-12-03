package com.example.batchExample.infrastructure.adapter.out.persistence;


import com.example.batchExample.application.dto.PersonOut;
import com.example.batchExample.application.port.out.GetPersonPort;
import com.example.batchExample.application.port.out.SavePersonPort;
import com.example.batchExample.infrastructure.adapter.out.persistence.repository.PersonRecordRepository;
import com.example.batchExample.infrastructure.mapper.PersonRecordMapper;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import io.github.resilience4j.core.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Service
@Slf4j
public class JpaSavePersonAdapter implements SavePersonPort, GetPersonPort {


    PersonRecordRepository personRecordRepository;
    PersonRecordMapper personRecordMapper = new PersonRecordMapper();

    public JpaSavePersonAdapter(PersonRecordRepository personRecordRepository){
        this.personRecordRepository = personRecordRepository;
    }


    @Override
    public void persistAll(List<PersonOut> items) {
        personRecordRepository.saveAll(personRecordMapper.mapListToRecords(items));
    }

//    @Retryable(
//            retryFor = {Exception.class, EOFException.class, ConnectException.class, IOException.class},
//            maxAttempts = 1,
//            listeners = "myRetryListener",
//            backoff = @Backoff(delay = 1000, multiplier = 2.0))
//    @Override
//    public List<PersonOut> getAllPersons() {
//        return personRecordMapper.mapToListOut(personRecordRepository.findAll());
//    }

//    @Recover  // using spring retry with
    public List<PersonOut> recover(Exception e) {
        log.warn("Todos los intentos han fallado. Se ejecuta el método de recuperación.");
        // Aquí puedes manejar el error, realizar una acción alternativa o notificar al usuario

        return List.of();
    }

    @Retry(name = "externalApiRetry", fallbackMethod = "recover")
    @Override
    public List<PersonOut> getAllPersons() {
        return personRecordMapper.mapToListOut(personRecordRepository.findAll());
    }

    /**
     * Combinación de múltiples patrones
     */
//    @CircuitBreaker(name = "externalService", fallbackMethod = "fallbackGetData")
//    @Retry(name = "externalApiRetry")
//    @RateLimiter(name = "apiRateLimiter")
//    @Bulkhead(name = "apiBulkhead")
//    public String getDataWithAllPatterns(String id) {
//        log.info("Llamada con todos los patrones, id: {}", id);
//        return "restClient.get()";
//    }

    private String fallbackGetData(String id, Exception ex) {
        log.warn("Fallback ejecutado para id: {}, Error: {}", id, ex.getMessage());
        return "{\"fallback\": \"Datos desde caché para id: " + id + "\"}";
    }

    private String rateLimiterFallback(String id, Exception ex) {
        log.warn("Rate limit alcanzado para id: {}", id);
        return "{\"error\": \"Rate limit excedido, intente más tarde\"}";
    }

    private String bulkheadFallback(String id, Exception ex) {
        log.warn("Bulkhead saturado para id: {}", id);
        return "{\"error\": \"Servicio ocupado, intente más tarde\"}";
    }

    private CompletionStage<String> timeLimiterFallback(String id, Exception ex) {
        log.warn("Timeout para id: {}", id);
        return CompletableFuture.completedFuture(
                "{\"error\": \"Timeout en procesamiento\"}"
        );
    }

    private void simulateProcessing(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}


