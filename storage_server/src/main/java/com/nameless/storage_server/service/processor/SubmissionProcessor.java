package com.nameless.storage_server.service.processor;

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

@Service
public class SubmissionProcessor {

    private static final Logger logger = LoggerFactory.getLogger(SubmissionProcessor.class);

    private final SubmissionService submissionService;
    private final ClazzService clazzService;
    private final ExtractManager extractManager;

    @Autowired
    public SubmissionProcessor(SubmissionService submissionService,
                               ClazzService clazzService,
                               ExtractManager extractManager) {
        this.submissionService = submissionService;
        this.clazzService = clazzService;
        this.extractManager = extractManager;
    }

    /**
     * Processes all submissions for a given project ID.
     *
     * @param projectId the ID of the project.
     */
    public void processSubmissionsByProject(Long projectId) {
        logger.info("Starting submission processing for project: {}", projectId);

        List<Submissions> submissions = submissionService.getSubmissionsByProjectIdAndProcessedFalse(projectId);
        if (submissions.isEmpty()) {
            logger.warn("No unprocessed submissions found for project: {}", projectId);
            return;
        }

        submissions.forEach(this::processSubmission);
    }

    /**
     * Processes a single submission.
     *
     * @param submission the submission to process.
     */
    private void processSubmission(Submissions submission) {
        logger.info("Processing submission with ID: {}", submission.getId());

        Path filePath = Path.of(submission.getFilePath());
        if (!Files.exists(filePath)) {
            logger.error("File does not exist: {}", filePath);
            return;
        }

        try {
            String fileContent = Files.readString(filePath);
            if (fileContent.isBlank()) {
                logger.warn("File content is empty for: {}", filePath);
                return;
            }

            Clazz clazz = clazzService.createClazz(extractClassName(submission.getFilePath()), submission.getFilePath(), submission.getProject());
            extractManager.extractTokens(fileContent, clazz);

            markSubmissionAsProcessed(submission);

        } catch (IOException e) {
            logger.error("Error reading file: {} - {}", filePath, e.getMessage());
        }
    }

    /**
     * Marks a submission as processed.
     *
     * @param submission the submission to update.
     */
    private void markSubmissionAsProcessed(Submissions submission) {
        submission.setProcessed(true);
        submissionService.saveSubmission(submission);
        logger.info("Submission marked as processed: {}", submission.getId());
    }

    /**
     * Extracts the class name from the file path.
     *
     * @param filePath the file path.
     * @return the class name.
     */
    private String extractClassName(String filePath) {
        return Path.of(filePath).getFileName().toString().replace(".java", "");
    }

    /**
     * Extracts the package name from the file content.
     *
     * @param fileContent the file content.
     * @return the package name, or an empty string if not found.
     */
    private String extractPackageName(String fileContent) {
        return fileContent.lines()
                .filter(line -> line.startsWith("package "))
                .map(line -> line.replace("package ", "").replace(";", "").trim())
                .findFirst()
                .orElse("");
    }
}
