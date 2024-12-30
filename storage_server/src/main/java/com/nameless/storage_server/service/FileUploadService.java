package com.nameless.storage_server.service;

import com.nameless.storage_server.Executor.ExecutorManager;
import com.nameless.storage_server.entity.Submissions;
import com.nameless.storage_server.queue.SubmissionsProducer;
import com.nameless.storage_server.repository.SubmissionsRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.logging.Logger;

/**
 * Service class for handling file upload and processing operations.
 * <p>
 * This service processes .zip files uploaded by users, extracts .java files,
 * stores them in a specified directory, and records their metadata in a database.
 */
@Service
public class FileUploadService {

    /** Directory where uploaded Java files will be stored. */
    private static final String STORAGE_DIRECTORY = "C:/Users/user/Desktop/System design training/spring/search engine/storage_server/uploaded_java_files/";

    /** Logger instance for logging events and errors. */
    private static final Logger logger = Logger.getLogger(FileUploadService.class.getName());

    private final SubmissionsRepository submissionsRepository;
    private final SubmissionsProducer submissionsProducer;
    private final ExecutorManager executorManager;

    /**
     * Constructs a FileUploadService with the specified dependencies.
     *
     * @param submissionsRepository the repository for storing submission metadata.
     * @param submissionsProducer the producer for sending messages to the queue.
     * @param executorManager the manager for handling thread pool operations.
     */
    public FileUploadService(SubmissionsRepository submissionsRepository, SubmissionsProducer submissionsProducer, ExecutorManager executorManager) {
        this.submissionsRepository = submissionsRepository;
        this.submissionsProducer = submissionsProducer;
        this.executorManager = executorManager;
    }

    /**
     * Processes an uploaded .zip file, extracting and storing .java files.
     *
     * @param file the uploaded .zip file.
     * @throws IOException if an I/O error occurs during processing.
     */
    public void processZipFileAndStoreJavaFiles(MultipartFile file) throws IOException {
        // Validate that the uploaded file is a .zip file
        validateZipFile(file);

        // Ensure the storage directory exists
        createStorageDirectory();

        // Process the zip file
        try (ZipInputStream zis = new ZipInputStream(file.getInputStream())) {
            processZipEntries(zis);
        }
    }

    /**
     * Validates that the uploaded file is a .zip file.
     *
     * @param file the uploaded file.
     * @throws IllegalArgumentException if the file is not a .zip file.
     */
    private void validateZipFile(MultipartFile file) {
        if (!file.getOriginalFilename().endsWith(".zip")) {
            throw new IllegalArgumentException("Please upload a .zip file");
        }
    }

    /**
     * Ensures that the storage directory exists, creating it if necessary.
     *
     * @throws IOException if an error occurs while creating the directory.
     */
    private void createStorageDirectory() throws IOException {
        Path storagePath = Paths.get(STORAGE_DIRECTORY);
        if (!Files.exists(storagePath)) {
            Files.createDirectories(storagePath);
        }
    }

    /**
     * Processes the entries of the provided .zip input stream, extracting and storing .java files.
     *
     * @param zis the input stream of the .zip file.
     * @throws IOException if an error occurs during processing.
     */
    private void processZipEntries(ZipInputStream zis) throws IOException {
        ZipEntry zipEntry;
        String projectName = null;
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        while ((zipEntry = zis.getNextEntry()) != null) {
            if (isJavaFile(zipEntry)) {
                if (projectName == null) {
                    projectName = extractProjectName(zipEntry.getName());
                    storeProjectNameInMessageQueue(projectName); // Save project name in MQ
                }

                String fullFilePath = constructFilePath(projectName, zipEntry.getName());
                futures.add(processJavaFileAsync(zis, fullFilePath, projectName));
            }
            zis.closeEntry();
        }

        // Wait for all tasks to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    /**
     * Checks if the given .zip entry is a .java file.
     *
     * @param zipEntry the .zip entry to check.
     * @return true if the entry is a .java file; false otherwise.
     */
    private boolean isJavaFile(ZipEntry zipEntry) {
        return !zipEntry.isDirectory() && zipEntry.getName().endsWith(".java");
    }

    /**
     * Extracts the project name from the given file path.
     *
     * @param filePath the file path to extract the project name from.
     * @return the extracted project name.
     */
    private String extractProjectName(String filePath) {
        int firstSlashIndex = filePath.indexOf('/');
        return firstSlashIndex != -1 ? filePath.substring(0, firstSlashIndex) : filePath;
    }

    /**
     * Constructs the full file path for a given project and file name.
     *
     * @param projectName the project name.
     * @param fileName the file name.
     * @return the constructed file path.
     */
    private String constructFilePath(String projectName, String fileName) {
        if (!fileName.startsWith(projectName)) {
            return STORAGE_DIRECTORY + projectName + "/" + fileName;
        } else {
            return STORAGE_DIRECTORY + fileName; // No need to add project name again
        }
    }

    /**
     * Asynchronously processes a .java file, saving it to disk and storing its metadata in the database.
     *
     * @param zis the input stream of the .zip file.
     * @param fullFilePath the full file path where the .java file will be stored.
     * @param projectName the name of the project the file belongs to.
     * @return a CompletableFuture representing the asynchronous task.
     */
    private CompletableFuture<Void> processJavaFileAsync(ZipInputStream zis, String fullFilePath, String projectName) {
        return CompletableFuture.runAsync(() -> {
            try {
                saveJavaFile(zis, fullFilePath);
                storeFilePathInDatabase(fullFilePath, projectName); // Store project name in DB
            } catch (IOException e) {
                logger.severe("Error processing file: " + fullFilePath + " - " + e.getMessage());
            }
        }, executorManager.getExecutorService());
    }

    /**
     * Saves a .java file to disk.
     *
     * @param zis the input stream of the .zip file.
     * @param fileName the name of the file to save.
     * @throws IOException if an error occurs during saving.
     */
    private void saveJavaFile(ZipInputStream zis, String fileName) throws IOException {
        Path filePath = Paths.get(fileName);
        Path parentDir = filePath.getParent();

        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }

        try (OutputStream os = Files.newOutputStream(filePath)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = zis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }

    /**
     * Stores the file path of a .java file in the database.
     *
     * @param filePath the file path to store.
     * @param projectName the name of the project the file belongs to.
     */
    private void storeFilePathInDatabase(String filePath, String projectName) {
        Submissions submission = new Submissions();
        submission.setFilePath(filePath);
        submission.setProcessed(false);
        submission.setProjectname(projectName); // Store project name directly in the submission
        submissionsRepository.save(submission);
    }

    /**
     * Logs and optionally stores the project name in the database.
     *
     * @param projectName the name of the project to store.
     */
    private void storeProjectNameInMessageQueue(String projectName) {
        logger.info("Storing project name in database: " + projectName);
        submissionsProducer.sendToQueue(projectName); // Notify queue
    }
}
