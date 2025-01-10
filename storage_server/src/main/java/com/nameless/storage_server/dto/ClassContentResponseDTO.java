package com.nameless.storage_server.dto;


public class ClassContentResponseDTO {
    private Long projectId;
    private String filePath;
    private String content;

    public ClassContentResponseDTO(String filePath, String content , Long projectId) {
        this.filePath = filePath;
        this.content = content;
        this.projectId = projectId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}