package com.nameless.storage_server.repository;

import com.nameless.storage_server.entity.NormalizeToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NormalizeTokenRepository extends JpaRepository<NormalizeToken, Long> {
}
