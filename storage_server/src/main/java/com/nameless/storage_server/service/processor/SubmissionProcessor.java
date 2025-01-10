package com.nameless.storage_server.service.processor;
import com.nameless.storage_server.entity.Submissions;
import com.nameless.storage_server.facade.manager.FacadeManager;
import com.nameless.storage_server.facade.enums.FacadeType;
import com.nameless.storage_server.service.submission.SubmissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class SubmissionProcessor {

    private static final Logger logger = LoggerFactory.getLogger(SubmissionProcessor.class);
    private final SubmissionService submissionService;
    private final FacadeManager facadeManager;

    @Autowired
    public SubmissionProcessor(SubmissionService submissionService,
                               FacadeManager facadeManager) {
        this.submissionService = submissionService;
        this.facadeManager = facadeManager;
    }

    public void processUnprocessedSubmissions(Long projectId) {
        logger.info("Starting submission processing for project: {}", projectId);
        List<Submissions> submissions = submissionService.getUnprocessedSubmissionsByProjectId(projectId);

        if (submissions.isEmpty()) {
            logger.warn("No unprocessed submissions found for project: {}", projectId);
            return;
        }

        submissions.forEach(submission -> facadeManager.execute(FacadeType.SUBMISSION, submission));
    }
}

