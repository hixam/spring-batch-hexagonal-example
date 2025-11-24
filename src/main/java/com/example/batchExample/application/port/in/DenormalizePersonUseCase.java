package com.example.batchExample.application.port.in;

import com.example.batchExample.application.dto.PersonOut;

public interface DenormalizePersonUseCase {
    PersonOut denormalizar(PersonOut input);
}
