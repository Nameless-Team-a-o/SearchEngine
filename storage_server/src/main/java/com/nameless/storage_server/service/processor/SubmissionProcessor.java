package com.nameless.storage_server.service.processor;
import com.nameless.storage_server.entity.Submissions;
import com.nameless.storage_server.facade.SubmissionFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class SubmissionProcessor {

    private static final Logger logger = LoggerFactory.getLogger(SubmissionProcessor.class);
    private final SubmissionFacade submissionFacade;

    @Autowired
    public SubmissionProcessor(SubmissionFacade submissionFacade) {
        this.submissionFacade = submissionFacade;
    }

    public void processUnprocessedSubmissions(Long projectId) {
        logger.info("Starting submission processing for project: {}", projectId);
        List<Submissions> submissions = submissionFacade.getUnprocessedSubmissionsByProjectId(projectId);

        if (submissions.isEmpty()) {
            logger.warn("No unprocessed submissions found for project: {}", projectId);
            return;
        }

        submissions.forEach(submissionFacade::processSubmission);
    }
}

