package com.nameless.storage_server.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String projectName;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Project(String projectName, User user) {
        this.projectName = projectName;
        this.user = user;
    }

    @PrePersist
    private void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

