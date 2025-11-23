package com.example.batchExample.application.port.in;

import com.example.batchExample.application.dto.PersonIn;
import com.example.batchExample.application.dto.PersonOut;

public interface NormalizePersonUseCase {
    PersonOut normalize(PersonIn input);
}
