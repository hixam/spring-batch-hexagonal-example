package com.example.batchExample.infrastructure.adapter.out.persistence.repository;

import com.example.batchExample.infrastructure.adapter.out.persistence.entity.PersonRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRecordRepository extends JpaRepository<PersonRecord, Long> {
    Page<PersonRecord> findByFirstName(String name, Pageable pageable);
    Page<PersonRecord> findByIdBetween(Long min, Long max, Pageable pageable);

    @Query("select min(p.id) from PersonRecord  p")
    Long findMinId();

    @Query("select max(p.id) from PersonRecord  p")
    Long findMaxId();
}
