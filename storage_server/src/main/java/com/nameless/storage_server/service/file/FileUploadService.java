package com.nameless.storage_server.service.file;

import com.nameless.storage_server.entity.Project;
import com.nameless.storage_server.entity.User;
import com.nameless.storage_server.facade.AuthenticationFacade;
import com.nameless.storage_server.service.processor.ZipProcessor;
import com.nameless.storage_server.service.project.ProjectService;
import com.nameless.storage_server.service.queue.producer.SubmissionsProducer;
import com.nameless.storage_server.service.storage.JavaFileSaver;
import com.nameless.storage_server.service.submission.SubmissionService;
import com.nameless.storage_server.service.validator.ZipValidator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.ZipInputStream;

/**
 * Service class for handling file upload and processing operations.
 */
@Service
@Transactional
public class FileUploadService {

    private final AuthenticationFacade authenticationFacade;
    private final ZipValidator zipValidator;
    private final ZipProcessor zipProcessor;
    private final JavaFileSaver javaFileSaver;
    private final ProjectService projectService;
    private final SubmissionService submissionService;
    private final SubmissionsProducer submissionsProducer;

    @Autowired
    public FileUploadService(AuthenticationFacade authenticationFacade,
                             ZipValidator zipValidator,
                             ZipProcessor zipProcessor,
                             JavaFileSaver javaFileSaver,
                             ProjectService projectService,
                             SubmissionService submissionService,
                             SubmissionsProducer submissionsProducer) {
        this.authenticationFacade = authenticationFacade;
        this.zipValidator = zipValidator;
        this.zipProcessor = zipProcessor;
        this.javaFileSaver = javaFileSaver;
        this.projectService = projectService;
        this.submissionService = submissionService;
        this.submissionsProducer = submissionsProducer;
    }

    public void processZipFile(MultipartFile file, String token) throws IOException {
        zipValidator.validateZipFile(file);

        User user = authenticationFacade.getUserFromToken(token);

        try (ZipInputStream zis = new ZipInputStream(file.getInputStream())) {
            AtomicReference<Project> projectRef = new AtomicReference<>();
            zipProcessor.processZipEntries(zis, zipEntry -> {
                if (zipEntry.isDirectory() && projectRef.get() == null) {
                    projectRef.set(projectService.createProject(projectService.extractProjectName(zipEntry.getName()), user));
                } else if (!zipEntry.isDirectory() && projectRef.get() != null && zipEntry.getName().endsWith(".java")) {
                    String filePath = null;
                    try {
                        filePath = javaFileSaver.saveJavaFile(zis, projectRef.get(), zipEntry.getName());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    submissionService.createSubmission(filePath, projectRef.get());
                }
            });

            if (projectRef.get() != null) {
                submissionsProducer.sendToQueue(projectRef.get().getProjectId());
            }
        }
    }
}
