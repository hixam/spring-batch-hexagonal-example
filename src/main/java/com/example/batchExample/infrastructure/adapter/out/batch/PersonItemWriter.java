package com.example.batchExample.infrastructure.adapter.out.batch;

import com.example.batchExample.application.dto.PersonOut;
import com.example.batchExample.application.port.in.PersistPersonsUseCase;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;

import java.util.List;

public class PersonItemWriter implements ItemWriter<PersonOut> {

    PersistPersonsUseCase persistPersonsUseCase;
    public PersonItemWriter(PersistPersonsUseCase persistUseCase) {
        this.persistPersonsUseCase = persistUseCase;
    }

    @Override
    public void write(Chunk<? extends PersonOut> chunk) {
        persistPersonsUseCase.persistAll((List<PersonOut>) chunk.getItems());
    }
}
