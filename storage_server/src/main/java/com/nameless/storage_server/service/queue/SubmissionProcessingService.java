package com.nameless.storage_server.service.queue;


import com.nameless.storage_server.entity.Clazz;
import com.nameless.storage_server.entity.Submissions;
import com.nameless.storage_server.repository.ClazzRepository;

import com.nameless.storage_server.repository.SubmissionsRepository;
import com.nameless.storage_server.service.file.JavaFileProcessorService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

@Service
public class SubmissionProcessingService {

    private final SubmissionsRepository submissionsRepository;
    private final ClazzRepository clazzRepository;
    private final JavaFileProcessorService fileProcessorService;
    private static final Logger logger = Logger.getLogger(SubmissionProcessingService.class.getName());

    public SubmissionProcessingService(SubmissionsRepository submissionsRepository, ClazzRepository clazzRepository, JavaFileProcessorService fileProcessorService) {
        this.submissionsRepository = submissionsRepository;
        this.clazzRepository = clazzRepository;
        this.fileProcessorService = fileProcessorService;
    }

    /**
     * Processes all files related to the given project name.
     *
     * @param projectId the name of the project.
     */
    public void processSubmissionsByProject(Long projectId) {
        logger.info("Fetching submissions for project: " + projectId);

        // Fetch all submissions for the given project
        List<Submissions> submissions = submissionsRepository.findByProjectIdAndProcessedFalse(projectId);

        if (submissions.isEmpty()) {
            logger.warning("No unprocessed submissions found for project: " + projectId);
            return;
        }

        submissions.forEach(this::processSubmission);
    }

    /**
     * Processes a single submission file, extracts its tokens, and updates the status.
     *
     * @param submission the submission to process.
     */
    private void processSubmission(Submissions submission) {
        try {
            logger.info("Processing submission: " + submission.getFilePath());

            // Check if the file exists before reading
            Path path = Path.of(submission.getFilePath());
            if (Files.exists(path)) {
                // Read file content
                String fileContent = Files.readString(path);

                if (fileContent.isEmpty()) {
                    logger.warning("File content is empty for file: " + submission.getFilePath());
                }

                // Save the file as a Clazz entity
                Clazz clazz = new Clazz();
                clazz.setClassName(extractClassName(submission.getFilePath()));
                clazz.setfilePath(submission.getFilePath());
                clazzRepository.save(clazz);

                // Log the file content for debugging
                logger.info("File Content: " + fileContent);

                // Process file content
                fileProcessorService.processFile(fileContent , clazz);

                // Mark submission as processed
                submission.setProcessed(true);
                submissionsRepository.save(submission);
            } else {
                logger.severe("File does not exist: " + submission.getFilePath());
            }

        } catch (IOException e) {
            logger.severe("Error reading file: " + submission.getFilePath() + " - " + e.getMessage());
        }
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
