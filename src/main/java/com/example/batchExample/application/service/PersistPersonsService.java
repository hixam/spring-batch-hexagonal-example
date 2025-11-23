package com.example.batchExample.application.service;

import com.example.batchExample.application.dto.PersonIn;
import com.example.batchExample.application.dto.PersonOut;
import com.example.batchExample.application.port.in.DenormalizePersonUseCase;
import com.example.batchExample.application.port.in.PersistPersonsUseCase;
import com.example.batchExample.application.port.out.GetPersonPort;
import com.example.batchExample.application.port.out.SavePersonPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersistPersonsService implements PersistPersonsUseCase {

    private final SavePersonPort savePersonPort;
    private final GetPersonPort getPersonPort;
    private final DenormalizePersonUseCase denormalizePersonUseCase;

    public PersistPersonsService(SavePersonPort savePersonPort, GetPersonPort getPersonPort, DenormalizePersonUseCase denormalizePersonUseCase){
        this.savePersonPort = savePersonPort;
        this.getPersonPort = getPersonPort;
        this.denormalizePersonUseCase = denormalizePersonUseCase;
    }

    @Override
    public void persistAll(List<PersonOut> input) {
        savePersonPort.persistAll(input);
    }

    @Override
    public List<PersonOut> getAll() {
        return getPersonPort.getAllPersons().stream()
                .map(denormalizePersonUseCase::denormalizar)
                .toList();
    }
}
