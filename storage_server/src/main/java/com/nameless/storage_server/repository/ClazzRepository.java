package com.nameless.storage_server.repository;

import com.nameless.storage_server.entity.Clazz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClazzRepository extends JpaRepository<Clazz,Long> {
    Optional<Clazz> findById(Long id);
}
