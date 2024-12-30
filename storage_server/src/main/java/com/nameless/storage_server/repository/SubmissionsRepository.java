package com.nameless.storage_server.repository;

import com.nameless.storage_server.entity.Submissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface SubmissionsRepository extends JpaRepository<Submissions,Long> {
}
