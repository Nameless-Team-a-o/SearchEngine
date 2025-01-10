package com.nameless.storage_server.service.storage;

import com.nameless.storage_server.entity.Project;
import com.nameless.storage_server.exception.JavaFileSaveException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipInputStream;

@Service
public class JavaFileSaver {

    private static final String STORAGE_DIRECTORY = "uploaded_java_files";

    public String saveJavaFile(ZipInputStream zis, Project project, String fileName) {
        try {
            String filePath = constructFilePath(project, fileName);
            Path path = Paths.get(filePath);

            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }

            try (OutputStream os = Files.newOutputStream(path)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = zis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }
            return filePath;
        } catch (IOException e) {
            throw new JavaFileSaveException("Failed to save Java file: " + fileName);
        }
    }

    private String constructFilePath(Project project, String fileName) {
        return Paths.get(
                STORAGE_DIRECTORY,
                project.getUser().getId().toString(),
                project.getProjectId().toString(),
                fileName
        ).toAbsolutePath().toString();
    }
}
