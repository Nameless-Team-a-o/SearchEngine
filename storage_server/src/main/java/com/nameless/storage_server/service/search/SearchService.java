package com.nameless.storage_server.service.search;

import com.nameless.storage_server.dto.SearchRequestDto;
import com.nameless.storage_server.entity.Clazz;
import com.nameless.storage_server.exception.AuthenticationException;
import com.nameless.storage_server.exception.FileOperationException;
import com.nameless.storage_server.facade.manager.FacadeManager;
import com.nameless.storage_server.facade.enums.FacadeType;
import com.nameless.storage_server.service.ResponseBuilder;
import com.nameless.storage_server.service.clazz.ClazzService;
import com.nameless.storage_server.service.file.FileReader;
import com.nameless.storage_server.service.jwt.JwtService;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service

public class SearchService {
    private final JwtService jwtService;
    private final ClazzService clazzService;
    private final FileReader fileReader;
    private final ResponseBuilder responseBuilder;
    private final FacadeManager facadeManager;


    public SearchService(JwtService jwtService,
                         ClazzService clazzService,
                         FileReader fileReader,
                         ResponseBuilder responseBuilder,
                         FacadeManager facadeManager) {
        this.jwtService = jwtService;
        this.clazzService = clazzService;
        this.fileReader = fileReader;
        this.responseBuilder = responseBuilder;
        this.facadeManager = facadeManager;
    }

    public ResponseEntity<?> searchTokens(SearchRequestDto searchDto, String token) {
        validateToken(token);
        return facadeManager.execute(FacadeType.SEARCH,searchDto);
    }

    public ResponseEntity<?> searchClass(Long id, String token) {
        validateToken(token);
        Clazz clazz = clazzService.findClazzById(id);

        try {
            String fileContent = fileReader.readFile(clazz.getFilePath());
            return responseBuilder.buildClassContentResponse(clazz.getFilePath(), fileContent, clazz.getProject().getProjectId() );
        } catch (FileOperationException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private void validateToken(String token) {
        if (!jwtService.validateToken(token)) {
            throw new AuthenticationException("Invalid token.");
        }
    }
}


