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

    public SubmissionProcessingService(SubmissionsRepository submissionsRepository,
                                       ClazzRepository clazzRepository,
                                       JavaFileProcessorService fileProcessorService) {
        this.submissionsRepository = submissionsRepository;
        this.clazzRepository = clazzRepository;
        this.fileProcessorService = fileProcessorService;
    }

    /**
     * Processes all submissions for a given project ID.
     *
     * @param projectId the ID of the project.
     */
    public void processSubmissionsByProject(Long projectId) {
        logger.info("Starting submission processing for project: " + projectId);

        List<Submissions> submissions = submissionsRepository.findByProjectIdAndProcessedFalse(projectId);
        if (submissions.isEmpty()) {
            logger.warning("No unprocessed submissions found for project: " + projectId);
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
        logger.info("Processing submission with ID: " + submission.getId());

        Path filePath = Path.of(submission.getFilePath());
        if (!Files.exists(filePath)) {
            logger.severe("File does not exist: " + filePath);
            return;
        }

        try {
            String fileContent = Files.readString(filePath);
            if (fileContent.isBlank()) {
                logger.warning("File content is empty for: " + filePath);
                return;
            }

            Clazz clazz = createClazzEntity(submission, fileContent);
            clazzRepository.save(clazz);

            fileProcessorService.processFile(fileContent, clazz);

            markSubmissionAsProcessed(submission);

        } catch (IOException e) {
            logger.severe("Error reading file: " + filePath + " - " + e.getMessage());
        }
    }

    /**
     * Creates a Clazz entity from a submission.
     *
     * @param submission  the submission.
     * @param fileContent the file content.
     * @return the Clazz entity.
     */
    private Clazz createClazzEntity(Submissions submission, String fileContent) {
        Clazz clazz = new Clazz();
        clazz.setClassName(extractClassName(submission.getFilePath()));
        clazz.setfilePath(submission.getFilePath());
        clazz.setProjectId(submission.getProjectId());
        return clazz;
    }

    /**
     * Marks a submission as processed.
     *
     * @param submission the submission to update.
     */
    private void markSubmissionAsProcessed(Submissions submission) {
        submission.setProcessed(true);
        submissionsRepository.save(submission);
        logger.info("Submission marked as processed: " + submission.getId());
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
