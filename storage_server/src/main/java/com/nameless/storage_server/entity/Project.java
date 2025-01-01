package com.nameless.storage_server.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    private String projectName;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }



    public String getProjectName() {
        return projectName;
    }

    public Long getProject_id() {
        return projectId;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

}

