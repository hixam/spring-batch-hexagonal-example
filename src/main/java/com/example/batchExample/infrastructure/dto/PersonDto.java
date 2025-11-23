package com.example.batchExample.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PersonDto {
    private final String firstName;
    private final String lastName;
}
