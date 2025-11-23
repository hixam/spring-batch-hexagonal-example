package com.example.batchExample.infrastructure.adapter.out.persistence;


import com.example.batchExample.application.dto.PersonOut;
import com.example.batchExample.application.port.out.GetPersonPort;
import com.example.batchExample.application.port.out.SavePersonPort;
import com.example.batchExample.infrastructure.adapter.out.persistence.repository.PersonRecordRepository;
import com.example.batchExample.infrastructure.mapper.PersonRecordMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
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

    @Override
    public List<PersonOut> getAllPersons() {
        return personRecordMapper.mapToListOut(personRecordRepository.findAll());
    }
}
