package com.nameless.storage_server.facade.interfaces;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public abstract class AbstractOperationFacade<REQUEST, RESPONSE> implements OperationFacade<REQUEST, RESPONSE> {

    @Override
    public ResponseEntity<RESPONSE> execute(REQUEST request) {
        try {
            // Template method pattern
            validateRequest(request);
            RESPONSE result = processRequest(request);
            return buildResponse(result);
        } catch (Exception e) {
            return handleError(e);
        }
    }

    protected abstract void validateRequest(REQUEST request);
    protected abstract RESPONSE processRequest(REQUEST request);
    protected abstract ResponseEntity<RESPONSE> buildResponse(RESPONSE result);
    protected abstract ResponseEntity<RESPONSE> handleError(Exception e);
}