package com.example.batchExample.infrastructure.config;

import com.example.batchExample.application.dto.PersonOut;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Slf4j
@Component
public class ShouldRetryPredicate implements Predicate<Object> {

    @Override
    public boolean test(Object result) {
        log.info("🔍 Evaluando resultado para retry: {}", result);


        if (result instanceof List) {
            List<?> list = (List<?>) result;

            // Opción A: Si la lista está vacía, reintentar
            if (list.isEmpty()) {
                return true; // Reintentar para obtener datos
            }

            // Opción B: Verificar si los elementos son del tipo PersonOut
            if (list.getFirst() instanceof PersonOut) {
                @SuppressWarnings("unchecked")
                List<PersonOut> personList = (List<PersonOut>) result;
                return false;
            }

            // Opción C: Evaluar lista genérica
            return false;
        }

        // Caso 1: Si el resultado es String
        if (result instanceof String) {
            //return log.info((String) result);
        }

        // Caso 3: Si el resultado es un número (código de error)
        if (result instanceof Integer) {
           // return evaluateNumericResult((Integer) result);
        }

        // Caso 4: Resultado booleano
        if (result instanceof Boolean) {
            return !((Boolean) result); // Si es false, reintentar
        }

        // Por defecto, no reintentar
        return false;    }
}
