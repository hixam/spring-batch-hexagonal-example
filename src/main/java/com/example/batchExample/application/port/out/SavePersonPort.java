package com.example.batchExample.application.port.out;

import com.example.batchExample.application.dto.PersonOut;

import java.util.List;

public interface SavePersonPort {

    void persistAll(List<PersonOut> items);
}
