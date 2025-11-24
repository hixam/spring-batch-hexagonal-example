package com.example.batchExample.infrastructure.adapter.in.batch;

import com.example.batchExample.application.dto.PersonIn;
import com.example.batchExample.application.dto.PersonOut;
import com.example.batchExample.application.port.in.NormalizePersonUseCase;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class PersonItemProcessor implements ItemProcessor<PersonIn, PersonOut> {

    private final NormalizePersonUseCase useCase;
    public PersonItemProcessor(NormalizePersonUseCase useCase) { this.useCase = useCase; }

    @Override
    public @Nullable PersonOut process(@NonNull PersonIn item) throws Exception {
        return useCase.normalize(item);
    }
}
