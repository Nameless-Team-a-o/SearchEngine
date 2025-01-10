package com.nameless.storage_server.service.clazz;

import com.nameless.storage_server.entity.Clazz;
import com.nameless.storage_server.entity.Project;
import com.nameless.storage_server.repository.ClazzRepository;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.IllformedLocaleException;
import java.util.Optional;

@Service
public class ClazzService {
    private final ClazzRepository clazzRepository;
    public ClazzService(
            final ClazzRepository clazzRepository) {
        this.clazzRepository = clazzRepository;
    }

    public Clazz createClazz(String className , String filePath , Project project) {
        return saveClazz(new Clazz(className,filePath, project));
    }

    public Clazz saveClazz(Clazz clazz) {
        return clazzRepository.save(clazz);
    }

    public Clazz findClazzById(Long id) {
        Clazz clazz = clazzRepository.findById(id).orElseThrow(() -> new RuntimeException("Class not found"));

        return clazz;
    }
}
