package com.nameless.storage_server.service.project;

import com.nameless.storage_server.entity.Project;
import com.nameless.storage_server.entity.User;
import com.nameless.storage_server.exception.ProjectCreationException;
import com.nameless.storage_server.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class ProjectService {

    private static final Logger logger = Logger.getLogger(ProjectService.class.getName());

    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Project createProject(String projectName, User user) {
        try {
            Project project = new Project(projectName, user);
            Project savedProject = projectRepository.save(project);
            logger.info("Stored project in database: " + projectName);
            return savedProject;
        } catch (Exception e) {
            throw new ProjectCreationException("Failed to create project: " + projectName);
        }
    }

    public String extractProjectName(String filePath) {
        int firstSlashIndex = filePath.indexOf('/');
        return firstSlashIndex != -1 ? filePath.substring(0, firstSlashIndex) : filePath;
    }

    public Project getProject(Long projectId) {
        return projectRepository.getReferenceById(projectId);
    }
}
