package com.nameless.storage_server.entity;

import jakarta.persistence.*;

@Entity
public class NormalizeToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;

    // Using Enum for type safety
    @Enumerated(EnumType.STRING)
    private TokenType type;

    private Long lineNumber;

    private Long classID;

    public void setToken(String token) {
        this.token = token;
    }

    public TokenType getType() {
        return type;
    }

    public Long getLineNumber() {
        return lineNumber;
    }



    public String getToken() {
        return token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public void setLineNumber(Long lineNumber) {
        this.lineNumber = lineNumber;
    }

    public Long getClassID() {
        return classID;
    }

    public void setClassID(Long classID) {
        this.classID = classID;
    }
}
