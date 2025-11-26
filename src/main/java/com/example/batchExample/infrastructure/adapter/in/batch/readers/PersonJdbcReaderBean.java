package com.example.batchExample.infrastructure.adapter.in.batch.readers;

import com.example.batchExample.application.dto.PersonIn;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;

import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Map;

@Component
public class PersonJdbcReaderBean {
    @Bean
    @StepScope
    public JdbcCursorItemReader<PersonIn> jdbcReader(DataSource dataSource) {
        return new JdbcCursorItemReaderBuilder<PersonIn>()
                .name("personJdbcCursorReader")
                .dataSource(dataSource)
                .sql("SELECT first_name, last_name FROM persons_out ORDER BY id")
                .rowMapper((rs, rowNum) -> {
                    PersonIn person = new PersonIn(
                            rs.getString("first_name"),
                            rs.getString("last_name"));
                    System.out.println("JdbcCursorItemReader -> " + person);
                    return person;
                })
                .build();
    }

    @Bean
    @StepScope
    public JdbcPagingItemReader<PersonIn> personJdbcPagingReader(DataSource ds) {
        Map<String, Order> sortKeys = Map.of("id", Order.ASCENDING);

        return new JdbcPagingItemReaderBuilder<PersonIn>()
                .name("personJdbcPagingReader")
                .dataSource(ds)
                .selectClause("SELECT id, first_name, last_name")
                .fromClause("FROM persons_out")
                .sortKeys(sortKeys)
                .rowMapper((rs, rowNum) -> new PersonIn(
                        rs.getString("first_name"),
                        rs.getString("last_name")))
                .pageSize(5)          // pages “page” de DB
                .fetchSize(10) // items per page
                .build();
    }
}
