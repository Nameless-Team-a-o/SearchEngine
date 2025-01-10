package com.nameless.storage_server.facade.interfaces;

import org.springframework.http.ResponseEntity;

public interface OperationFacade<REQUEST, RESPONSE> {
    ResponseEntity<RESPONSE> execute(REQUEST request);
}