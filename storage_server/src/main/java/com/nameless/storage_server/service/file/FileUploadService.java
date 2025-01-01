package com.nameless.storage_server.service.file;

import com.nameless.storage_server.entity.Project;
import com.nameless.storage_server.entity.Submissions;
import com.nameless.storage_server.entity.User;
import com.nameless.storage_server.repository.UserRepository;
import com.nameless.storage_server.service.jwt.JwtService;
import com.nameless.storage_server.service.queue.SubmissionsProducer;
import com.nameless.storage_server.repository.ProjectRepository;
import com.nameless.storage_server.repository.SubmissionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
 */
@Service
public class FileUploadService {

    private static final String STORAGE_DIRECTORY =
            "C:/Users/user/Desktop/System design training/spring/search engine/storage_server/uploaded_java_files/";
    private static final Logger logger = Logger.getLogger(FileUploadService.class.getName());

    private final SubmissionsRepository submissionsRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final SubmissionsProducer submissionsProducer;

    @Autowired
    public FileUploadService(SubmissionsRepository submissionsRepository,
                             ProjectRepository projectRepository,
                             UserRepository userRepository,
                             JwtService jwtService,
                             SubmissionsProducer submissionsProducer) {
        this.submissionsRepository = submissionsRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.submissionsProducer = submissionsProducer;
    }

    /**
     * Processes an uploaded .zip file, extracting and storing .java files.
     *
     * @param file  the uploaded .zip file.
     * @param token the user's JWT token.
     * @throws IOException if an I/O error occurs during processing.
     */
    public void processZipFileAndStoreJavaFiles(MultipartFile file, String token) throws IOException {
        logger.info("Processing uploaded zip file: " + file.getOriginalFilename());
        validateZipFile(file);

        String username = jwtService.extractUsernameFromAccess(token.substring(7));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        createStorageDirectory();

        try (ZipInputStream zis = new ZipInputStream(file.getInputStream())) {
            processZipEntries(zis, user.getId());
        } catch (IOException e) {
            logger.severe("Error processing zip file: " + e.getMessage());
            throw e;
        }
    }

    private void validateZipFile(MultipartFile file) {
        if (!file.getOriginalFilename().endsWith(".zip")) {
            logger.warning("Invalid file type: " + file.getOriginalFilename());
            throw new IllegalArgumentException("Uploaded file must be a .zip file.");
        }
    }

    private void createStorageDirectory() throws IOException {
        Path storagePath = Paths.get(STORAGE_DIRECTORY);
        if (!Files.exists(storagePath)) {
            Files.createDirectories(storagePath);
            logger.info("Created storage directory: " + storagePath);
        }
    }

    private void processZipEntries(ZipInputStream zis, Long userId) throws IOException {
        ZipEntry zipEntry;
        String projectName = null;

        while ((zipEntry = zis.getNextEntry()) != null) {
            if (isJavaFile(zipEntry)) {
                if (projectName == null) {
                    projectName = extractProjectName(zipEntry.getName());
                    storeProjectInDatabase(projectName, userId);
                }

                String filePath = constructFilePath(projectName, zipEntry.getName(), userId);
                saveJavaFile(zis, filePath);
                storeFilePathInDatabase(filePath, projectName);
            }
            zis.closeEntry();
        }

        enqueueProjectForProcessing(projectName);
    }

    private boolean isJavaFile(ZipEntry zipEntry) {
        return !zipEntry.isDirectory() && zipEntry.getName().endsWith(".java");
    }

    private String extractProjectName(String filePath) {
        int firstSlashIndex = filePath.indexOf('/');
        return firstSlashIndex != -1 ? filePath.substring(0, firstSlashIndex) : filePath;
    }

    private void storeProjectInDatabase(String projectName, Long userId) {
        Project project = new Project();
        project.setProjectName(projectName);
        project.setUserId(userId);
        projectRepository.save(project);
        logger.info("Stored project in database: " + projectName);
    }

    private String constructFilePath(String projectName, String fileName, Long userId) {
        return String.format("%s/%d/%s/%s",
                STORAGE_DIRECTORY, userId, projectName, fileName);
    }

    private void saveJavaFile(ZipInputStream zis, String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }

        try (OutputStream os = Files.newOutputStream(path)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = zis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
        logger.info("Saved Java file: " + filePath);
    }

    private void storeFilePathInDatabase(String filePath, String projectName) {
        Project project = projectRepository.findTopByProjectNameOrderByCreatedAtDesc(projectName)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectName));

        Submissions submission = new Submissions();
        submission.setFilePath(filePath);
        submission.setProcessed(false);
        submission.setProjectId(project.getProject_id());
        submissionsRepository.save(submission);

        logger.info("Stored file metadata in database: " + filePath);
    }

    private void enqueueProjectForProcessing(String projectName) {
        Project project = projectRepository.findTopByProjectNameOrderByCreatedAtDesc(projectName)
                .orElseThrow(() -> new IllegalArgumentException("Project not found for queueing: " + projectName));

        submissionsProducer.sendToQueue(project.getProject_id());
        logger.info("Enqueued project for processing: " + project.getProject_id());
    }
}
