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
    private String projectname;

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public void setProjectname(String projectname) {
        this.projectname = projectname;
    }
}
