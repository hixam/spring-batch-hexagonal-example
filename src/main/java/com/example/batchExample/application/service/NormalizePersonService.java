package com.example.batchExample.application.service;

import com.example.batchExample.application.dto.PersonIn;
import com.example.batchExample.application.dto.PersonOut;
import com.example.batchExample.application.port.in.NormalizePersonUseCase;
import com.example.batchExample.domain.Person;
import org.springframework.stereotype.Service;

@Service
public class NormalizePersonService implements NormalizePersonUseCase{


    @Override
    public PersonOut normalize(PersonIn input) {
        Person person = new Person(input.firstName(), input.lastName()).normalized();
        return new PersonOut(person.getFirstName(), person.getLastName());
    }
}
