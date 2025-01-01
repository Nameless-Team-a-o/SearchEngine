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



    public void setfilePath(String packageName) {
        this.filePath = packageName;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
