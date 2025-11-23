package com.example.batchExample.infrastructure.config;

import com.example.batchExample.application.service.DenormalizePersonService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppBeanConfig {
//TODO: remove all beans annotations from app layer and inmpl. here
    @Bean
    public DenormalizePersonService denormalizePersonService(){
        return new DenormalizePersonService();
    }

}
