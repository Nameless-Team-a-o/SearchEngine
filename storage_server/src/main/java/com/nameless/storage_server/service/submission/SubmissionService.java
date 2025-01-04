package com.nameless.storage_server.service.submission;

import com.nameless.storage_server.entity.Project;
import com.nameless.storage_server.entity.Submissions;
import com.nameless.storage_server.repository.SubmissionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class SubmissionService {
    private static final Logger logger = Logger.getLogger(SubmissionService.class.getName());

    SubmissionsRepository submissionsRepository;

    @Autowired
    public SubmissionService(SubmissionsRepository submissionsRepository) {
        this.submissionsRepository = submissionsRepository;
    }

    public void createSubmission(String filePath, Project project) {
        Submissions submission = new Submissions(filePath, project);
        saveSubmission(submission);

        logger.info("Stored file metadata in database: " + filePath);
    }
    public List<Submissions> getSubmissionsByProjectIdAndProcessedFalse( Long projectId) {
        return submissionsRepository.findByProject_ProjectIdAndProcessedFalse(projectId);
    }

    public void saveSubmission(Submissions submission) {
        submissionsRepository.save(submission);
    }
}
