package com.example.batchExample.infrastructure.adapter.out.persistence.repository;

import com.example.batchExample.infrastructure.adapter.out.persistence.entity.PersonRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRecordRepository extends JpaRepository<PersonRecord, Long> {
}
