package com.nameless.storage_server.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "class")
public class Clazz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String className;
    private String filePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @OneToMany(mappedBy = "clazz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Token> tokens = new ArrayList<>();

    @OneToMany(mappedBy = "clazz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NormalizeToken> normalizeTokens = new ArrayList<>();

    public Clazz(String className, String filePath, Project project) {
        this.className = className;
        this.filePath = filePath;
        this.project = project;
    }

    public Clazz() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<NormalizeToken> getNormalizeTokens() {
        return normalizeTokens;
    }

    public void setNormalizeTokens(List<NormalizeToken> normalizeTokens) {
        this.normalizeTokens = normalizeTokens;
    }
}
