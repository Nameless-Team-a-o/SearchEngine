package com.nameless.storage_server.repository;

import com.nameless.storage_server.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByProjectName(String projectName);
    Optional<Project> findTopByProjectNameOrderByCreatedAtDesc(String projectName);

}
