package com.example.batchExample.infrastructure.mapper;


import com.example.batchExample.application.dto.PersonOut;
import com.example.batchExample.infrastructure.adapter.out.persistence.entity.PersonRecord;
import com.example.batchExample.infrastructure.dto.PersonDto;

import java.util.List;

public class PersonRecordMapper {

    public PersonRecord mappToRecord(PersonOut input){
        return new PersonRecord(input.firstName(), input.lastName());
    }

    public PersonOut mapToOut(PersonRecord input){
        return new PersonOut(input.getFirstName(), input.getLastName());
    }

    public List<PersonOut> mapToListOut(List<PersonRecord> list){
        return list.stream().map(this::mapToOut).toList();
    }

    public PersonDto mapToDTO(PersonRecord input){
        return new PersonDto(input.getFirstName(), input.getLastName());
    }

    public List<PersonDto> mapRecordToListDTO(List<PersonRecord> inputList){
        return inputList.stream().map(this::mapToDTO).toList();
    }

    public List<PersonDto> mapToListDTO(List<PersonOut> inputList){
        return inputList.stream().map(this::mapToDTO).toList();
    }

    public PersonDto mapToDTO(PersonOut input){
        return new PersonDto(input.firstName(), input.lastName());
    }

    public List<PersonRecord> mapListToRecords(List<PersonOut> input){
        return input.stream().map(this::mappToRecord).toList();
    }
}
