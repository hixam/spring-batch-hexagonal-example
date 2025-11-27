package com.example.batchExample.infrastructure.adapter.in.batch.processor;

import com.example.batchExample.application.dto.PersonOut;
import com.example.batchExample.infrastructure.adapter.out.persistence.entity.PersonRecord;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class PersonEntityProcessor implements ItemProcessor<PersonRecord, PersonOut> {

    @Override
    public PersonOut process(PersonRecord item) throws Exception {
        return item.getFirstName().equalsIgnoreCase("HICHAM") ? new PersonOut(item.getFirstName(), item.getLastName()) : null;
    }
}
