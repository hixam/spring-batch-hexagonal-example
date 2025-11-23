package com.example.batchExample.application.port.in;

import com.example.batchExample.application.dto.PersonOut;

import java.util.List;

public interface PersistPersonsUseCase {
    void persistAll(List<PersonOut> input);
    List<PersonOut> getAll();
}
