package com.example.batchExample.infrastructure.adapter.out.persistence.repository;

import com.example.batchExample.infrastructure.adapter.out.persistence.entity.PersonRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRecordRepository extends JpaRepository<PersonRecord, Long> {
    Page<PersonRecord> findByFirstName(String name, Pageable pageable);
}
