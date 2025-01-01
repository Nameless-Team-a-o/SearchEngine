package com.nameless.storage_server.service.file;

import com.nameless.storage_server.entity.Project;
import com.nameless.storage_server.entity.Submissions;
import com.nameless.storage_server.service.queue.SubmissionsProducer;
import com.nameless.storage_server.repository.ProjectRepository;
import com.nameless.storage_server.repository.SubmissionsRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
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
    private final ProjectRepository projectRepository;
    private final SubmissionsProducer submissionsProducer;

    /**
     * Constructs a FileUploadService with the specified dependencies.
     *
     * @param submissionsRepository the repository for storing submission metadata.
     * @param submissionsProducer the producer for sending messages to the queue.
     */
    public FileUploadService(SubmissionsRepository submissionsRepository, ProjectRepository projectRepository, SubmissionsProducer submissionsProducer) {
        this.submissionsRepository = submissionsRepository;
        this.projectRepository = projectRepository;
        this.submissionsProducer = submissionsProducer;
    }

    /**
     * Processes an uploaded .zip file, extracting and storing .java files.
     *
     * @param file the uploaded .zip file.
     * @throws IOException if an I/O error occurs during processing.
     */
    public void processZipFileAndStoreJavaFiles(MultipartFile file) throws IOException {
        logger.info("Starting to process uploaded zip file: " + file.getOriginalFilename());
        // Validate that the uploaded file is a .zip file
        validateZipFile(file);

        // Ensure the storage directory exists
        createStorageDirectory();

        // Process the zip file
        try (ZipInputStream zis = new ZipInputStream(file.getInputStream())) {
            processZipEntries(zis);
        } catch (Exception e) {
            logger.severe("Error while processing zip file: " + e.getMessage());
            throw e;
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
            logger.warning("Invalid file type uploaded: " + file.getOriginalFilename());
            throw new IllegalArgumentException("Please upload a .zip file");
        }
        logger.info("Uploaded file is a valid .zip file: " + file.getOriginalFilename());
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
            logger.info("Created storage directory: " + storagePath);
        } else {
            logger.info("Storage directory already exists: " + storagePath);
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

        while ((zipEntry = zis.getNextEntry()) != null) {
            logger.info("Processing zip entry: " + zipEntry.getName());
            if (isJavaFile(zipEntry)) {
                if (projectName == null) {
                    projectName = extractProjectName(zipEntry.getName());
                    logger.info("Extracted project name: " + projectName);
                    storeProjectIdInDatabase(projectName); // Save project name in DB
                }

                String fullFilePath = constructFilePath(projectName, zipEntry.getName());
                logger.info("Constructed file path: " + fullFilePath);
                processJavaFile(zis, fullFilePath, projectName); // Process file synchronously
            }
            zis.closeEntry();
        }
        Optional<Project> project = projectRepository.findTopByProjectNameOrderByCreatedAtDesc(projectName);
        if (project.isPresent()) {
            storeProjectIdInMessageQueue(project.get().getProject_id());
        } else {
            logger.warning("Project not found in database: " + projectName);
        }
    }

    /**
     * Checks if the given .zip entry is a .java file.
     *
     * @param zipEntry the .zip entry to check.
     * @return true if the entry is a .java file; false otherwise.
     */
    private boolean isJavaFile(ZipEntry zipEntry) {
        boolean isJava = !zipEntry.isDirectory() && zipEntry.getName().endsWith(".java");
        if (isJava) {
            logger.info("Identified Java file: " + zipEntry.getName());
        }
        return isJava;
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
        Optional<Project> project = projectRepository.findTopByProjectNameOrderByCreatedAtDesc(projectName);
        if (project.isPresent()) {
            if (!fileName.startsWith(projectName)) {
                return STORAGE_DIRECTORY + project.get().getProject_id() + "/" + projectName + "/" + fileName;
            } else {
                return STORAGE_DIRECTORY + project.get().getProject_id() + "/" + fileName; // No need to add project name again
            }
        } else {
            logger.warning("Project not found when constructing file path: " + projectName);
            return null;
        }
    }

    /**
     * Processes a .java file, saving it to disk and storing its metadata in the database.
     *
     * @param zis the input stream of the .zip file.
     * @param fullFilePath the full file path where the .java file will be stored.
     * @param projectName the name of the project the file belongs to.
     * @throws IOException if an error occurs during processing.
     */
    private void processJavaFile(ZipInputStream zis, String fullFilePath, String projectName) throws IOException {
        try {
            saveJavaFile(zis, fullFilePath);
            storeFilePathInDatabase(fullFilePath, projectName); // Store project name in DB
        } catch (IOException e) {
            logger.severe("Error processing file: " + fullFilePath + " - " + e.getMessage());
        }
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

        // Create parent directories if they don't exist
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
            logger.info("Created directory: " + parentDir.toString());
        }

        // Write the file content to disk
        try (OutputStream os = Files.newOutputStream(filePath)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = zis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
        logger.info("File saved: " + fileName);
    }

    /**
     * Stores the file path of a .java file in the database.
     *
     * @param filePath the file path to store.
     * @param projectName the name of the project the file belongs to.
     */
    private void storeFilePathInDatabase(String filePath, String projectName) {
        Optional<Project> projectOptional = projectRepository.findTopByProjectNameOrderByCreatedAtDesc(projectName);
        if (projectOptional.isPresent()) {
            Project project = projectOptional.get();
            // Create and save the submission associated with the project
            Submissions submission = new Submissions();
            submission.setFilePath(filePath);
            submission.setProcessed(false);
            submission.setProjectId(project.getProject_id());
            submissionsRepository.save(submission);
            logger.info("File path stored in database: " + filePath);
        } else {
            logger.warning("Could not find project to store file path: " + projectName);
        }
    }
    /**
     * Logs and optionally stores the project ID in the MessageQueue.
     *
     * @param projectName the ID of the project to store.
     */
    private void storeProjectIdInDatabase(String projectName) {
        Project project = new Project();
        project.setProjectName(projectName);
        projectRepository.save(project);
    }

    private void storeProjectIdInMessageQueue(Long projectId) {
        logger.info("Storing project ID in MessageQueue: " + projectId);
        submissionsProducer.sendToQueue(projectId); // Notify queue
    }
}
