

package com.nameless.storage_server.facade.manager;

import com.nameless.storage_server.facade.enums.FacadeType;
import com.nameless.storage_server.facade.interfaces.OperationFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FacadeManager {
    private final Map<FacadeType, OperationFacade<?, ?>> facadeRegistry;

    public FacadeManager(Map<FacadeType, OperationFacade<?, ?>> facadeRegistry) {
        this.facadeRegistry = facadeRegistry;
    }

    @SuppressWarnings("unchecked")
    public <REQ, RESP> OperationFacade<REQ, RESP> getFacade(FacadeType facadeType) {
        OperationFacade<?, ?> facade = facadeRegistry.get(facadeType);
        if (facade == null) {
            throw new IllegalArgumentException("Unknown facade type: " + facadeType);
        }
        return (OperationFacade<REQ, RESP>) facade;
    }

    public <REQ, RESP> ResponseEntity<RESP> execute(FacadeType facadeType, REQ request) {
        OperationFacade<REQ, RESP> facade = getFacade(facadeType);
        return facade.execute(request);
    }
}
