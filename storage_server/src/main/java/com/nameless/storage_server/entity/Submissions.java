package com.nameless.storage_server.entity;

import jakarta.persistence.*;


import lombok.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Submissions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String filePath;
    private boolean processed;

    private Long projectId;

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getFilePath() {
        return filePath;
    }

    public boolean isProcessed() {
        return processed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProject_Id() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
