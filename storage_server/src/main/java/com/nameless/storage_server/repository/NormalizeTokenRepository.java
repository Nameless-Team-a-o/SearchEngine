package com.nameless.storage_server.repository;

import com.nameless.storage_server.entity.NormalizeToken;
import com.nameless.storage_server.entity.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NormalizeTokenRepository extends JpaRepository<NormalizeToken, Long> {
    List<NormalizeToken> findTop10ByTokenStartingWithOrderByCreatedDateDesc(String searchTerm);
    List <NormalizeToken> findTop10ByToken(String token);
    List<NormalizeToken> findTop10ByTokenAndType(String token, TokenType type);


}
