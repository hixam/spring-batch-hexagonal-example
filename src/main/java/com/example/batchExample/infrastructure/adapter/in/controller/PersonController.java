package com.example.batchExample.infrastructure.adapter.in.controller;


import com.example.batchExample.application.port.in.PersistPersonsUseCase;
import com.example.batchExample.infrastructure.dto.PersonDto;
import com.example.batchExample.infrastructure.mapper.PersonRecordMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {

    PersistPersonsUseCase persistPersonsUseCase;
    PersonRecordMapper personRecordMapper = new PersonRecordMapper();

    public PersonController(PersistPersonsUseCase persistPersonsUseCase){this.persistPersonsUseCase = persistPersonsUseCase;}


    @GetMapping("/all")
    public ResponseEntity<List<PersonDto>> getPersonOut(){
        List<PersonDto> response = personRecordMapper.mapToListDTO(persistPersonsUseCase.getAll());
        return ResponseEntity.ok(response);
    }
}
