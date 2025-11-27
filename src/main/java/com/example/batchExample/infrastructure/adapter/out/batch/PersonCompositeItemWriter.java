package com.example.batchExample.infrastructure.adapter.out.batch;

import com.example.batchExample.application.dto.PersonOut;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PersonCompositeItemWriter {

    @Bean(name = "compositeWriter")
    public CompositeItemWriter<PersonOut> compositeWriter(
            @Qualifier("personItemWriter") ItemWriter<PersonOut> personWriter,
            FlatFileItemWriter<PersonOut> personCsvWriter) {

        CompositeItemWriter<PersonOut> writer = new CompositeItemWriter<>();
        writer.setDelegates(List.of(personWriter, personCsvWriter));
        return writer;
    }
}
