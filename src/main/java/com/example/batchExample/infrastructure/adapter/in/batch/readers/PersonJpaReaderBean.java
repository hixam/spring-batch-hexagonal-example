package com.example.batchExample.infrastructure.adapter.in.batch.readers;

import com.example.batchExample.infrastructure.adapter.out.persistence.entity.PersonRecord;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class PersonJpaReaderBean {

    @Bean
    @StepScope //need a processor to converto from entity to personOut
    public JpaPagingItemReader<PersonRecord> personJpaPagingReader(EntityManagerFactory emf) {
        JpaPagingItemReader<PersonRecord> reader = new JpaPagingItemReader<>();
        reader.setName("personJpaPagingReader");
        reader.setEntityManagerFactory(emf);
        reader.setQueryString("SELECT p FROM PersonRecord p ORDER BY p.id");
        reader.setPageSize(100);
        return reader;
    }

}
