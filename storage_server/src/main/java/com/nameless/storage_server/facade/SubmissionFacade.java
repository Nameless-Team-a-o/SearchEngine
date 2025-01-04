package com.nameless.storage_server.facade;

import com.nameless.storage_server.entity.Clazz;
import com.nameless.storage_server.entity.Submissions;
import com.nameless.storage_server.service.clazz.ClazzService;
import com.nameless.storage_server.service.extractors.ExtractManager;
import com.nameless.storage_server.service.submission.SubmissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Service
public class SubmissionFacade {

    private final SubmissionService submissionService;
    private final ClazzService clazzService;
    private final ExtractManager extractManager;
    private static final Logger logger = LoggerFactory.getLogger(SubmissionFacade.class);


    @Autowired
    public SubmissionFacade(SubmissionService submissionService,
                            ClazzService clazzService,
                            ExtractManager extractManager) {
        this.submissionService = submissionService;
        this.clazzService = clazzService;
        this.extractManager = extractManager;
    }


    public List<Submissions> getUnprocessedSubmissionsByProjectId(Long projectId) {
        return submissionService.getUnprocessedSubmissionsByProjectId(projectId);
    }

    public void processSubmission(Submissions submission) {
        logger.info("Processing submission with ID: {}", submission.getId());

        Path filePath = Path.of(submission.getFilePath());
        Optional<String> fileContentOpt = readFileContent(filePath);

        if (fileContentOpt.isEmpty()) return;

        String fileContent = fileContentOpt.get();
        if (fileContent.isBlank()) {
            logger.warn("File content is empty for: {}", filePath);
            return;
        }

        Clazz clazz = createClazz(submission);
        extractManager.extractTokens(fileContent, clazz);
        markSubmissionAsProcessed(submission);
    }

    private Optional<String> readFileContent(Path filePath) {
        try {
            if (!Files.exists(filePath)) {
                logger.error("File does not exist: {}", filePath);
                return Optional.empty();
            }
            return Optional.of(Files.readString(filePath));
        } catch (IOException e) {
            logger.error("Error reading file: {} - {}", filePath, e.getMessage());
            return Optional.empty();
        }
    }

    private Clazz createClazz(Submissions submission) {
        String className = extractClassName(submission.getFilePath());
        return clazzService.createClazz(className, submission.getFilePath(), submission.getProject());
    }

    private void markSubmissionAsProcessed(Submissions submission) {
        submission.setProcessed(true);
        submissionService.saveSubmission(submission);
        logger.info("Submission marked as processed: {}", submission.getId());
    }

    private String extractClassName(String filePath) {
        return Path.of(filePath).getFileName().toString().replace(".java", "");
    }
}