package com.nameless.storage_server.repository;

import com.nameless.storage_server.entity.Submissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionsRepository extends JpaRepository<Submissions, Long> {

    /**
     * Finds all unprocessed submissions by project name.
     *
     * @param projectId the project name.
     * @return a list of unprocessed submissions.
     */
    List<Submissions> findByProject_ProjectIdAndProcessedFalse(Long projectId);



}
