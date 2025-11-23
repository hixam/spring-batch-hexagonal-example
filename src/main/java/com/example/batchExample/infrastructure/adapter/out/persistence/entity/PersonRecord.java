package com.example.batchExample.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "person_out")
public class PersonRecord {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    protected PersonRecord() {} // JPA
    public PersonRecord(String firstName, String lastName) {
        this.firstName = firstName; this.lastName = lastName;
    }
    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
}
