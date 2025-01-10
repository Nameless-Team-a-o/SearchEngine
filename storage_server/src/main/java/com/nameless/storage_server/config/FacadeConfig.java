package com.nameless.storage_server.config;

import com.nameless.storage_server.facade.*;
import com.nameless.storage_server.facade.enums.FacadeType;
import com.nameless.storage_server.facade.interfaces.OperationFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class FacadeConfig {

    @Bean
    public Map<FacadeType, OperationFacade<?, ?>> facadeRegistry(
            SearchFacade searchFacade,
            AuthenticationFacade authFacade,
            SubmissionFacade submissionFacade
    ) {
        Map<FacadeType, OperationFacade<?, ?>> registry = new HashMap<>();
        registry.put(FacadeType.SEARCH, searchFacade);
        registry.put(FacadeType.AUTH, authFacade);
        registry.put(FacadeType.SUBMISSION, submissionFacade);
        return registry;
    }
}
