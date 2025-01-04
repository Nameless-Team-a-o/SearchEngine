package com.nameless.storage_server.repository;

import com.nameless.storage_server.entity.Clazz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClazzRepository extends JpaRepository<Clazz,Long> {
}
