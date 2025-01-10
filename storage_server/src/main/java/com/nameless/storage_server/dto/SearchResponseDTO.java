package com.nameless.storage_server.dto;

public class SearchResponseDTO {

    private String classId;
    private String className;
    private String tokenType;
    private String tokenInfo;
    private long lineNumber;
    private String filePath;

    // Getters and Setters

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getTokenInfo() {
        return tokenInfo;
    }

    public void setTokenInfo(String tokenInfo) {
        this.tokenInfo = tokenInfo;
    }

    public long getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(long lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    // Constructor
    public SearchResponseDTO(String classId, String className, String tokenType, String tokenInfo, long lineNumber, String filePath) {
        this.classId = classId;
        this.className = className;
        this.tokenType = tokenType;
        this.tokenInfo = tokenInfo;
        this.lineNumber = lineNumber;
        this.filePath = filePath;
    }
}
