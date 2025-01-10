package com.nameless.storage_server.facade;

import com.nameless.storage_server.entity.Clazz;
import com.nameless.storage_server.entity.Submissions;
import com.nameless.storage_server.exception.FileOperationException;
import com.nameless.storage_server.facade.interfaces.OperationFacade;
import com.nameless.storage_server.service.clazz.ClazzService;
import com.nameless.storage_server.service.extractors.ExtractManager;
import com.nameless.storage_server.service.file.FileReader;
import com.nameless.storage_server.service.submission.SubmissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class SubmissionFacade implements OperationFacade<Submissions, Void> {

    private final SubmissionService submissionService;
    private final ClazzService clazzService;
    private final ExtractManager extractManager;
    private final FileReader fileReader;

    public SubmissionFacade(SubmissionService submissionService,
                            ClazzService clazzService,
                            ExtractManager extractManager,
                            FileReader fileReader) {
        this.submissionService = submissionService;
        this.clazzService = clazzService;
        this.extractManager = extractManager;
        this.fileReader = fileReader;
    }

    @Override
    public ResponseEntity<Void> execute(Submissions submission) {
        try {
            processSubmission(submission);
            return ResponseEntity.ok().build();
        } catch (FileOperationException e) {
            return ResponseEntity.internalServerError().build(); // Delegates to GlobalExceptionHandler
        }
    }

    private void processSubmission(Submissions submission) {
        String fileContent = fileReader.readFile(submission.getFilePath());
        Clazz clazz = clazzService.createClazz(
                extractClassName(submission.getFilePath()),
                submission.getFilePath(),
                submission.getProject()
        );
        extractManager.extractTokens(fileContent, clazz);
        submission.setProcessed(true);
        submissionService.saveSubmission(submission);
    }

    private String extractClassName(String filePath) {
        return Path.of(filePath).getFileName().toString().replace(".java", "");
    }
}
