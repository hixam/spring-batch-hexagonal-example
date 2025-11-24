package com.example.batchExample.application.service;

import com.example.batchExample.application.dto.PersonOut;
import com.example.batchExample.application.port.in.DenormalizePersonUseCase;
import com.example.batchExample.domain.Person;

public class DenormalizePersonService implements DenormalizePersonUseCase {

    @Override
    public PersonOut denormalizar(PersonOut input) {
        Person person = new Person(input.firstName(), input.lastName()).denormalize();
        return new PersonOut(person.getFirstName(), person.getLastName());    }
}
