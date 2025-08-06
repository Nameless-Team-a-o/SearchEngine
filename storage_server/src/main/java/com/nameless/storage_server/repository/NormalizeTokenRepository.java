package com.nameless.storage_server.repository;

import com.nameless.storage_server.entity.token.NormalizedToken;
import com.nameless.storage_server.entity.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NormalizeTokenRepository extends JpaRepository<NormalizedToken, Long> {
    List<NormalizedToken> findTop10ByTokenStartingWithOrderByCreatedDateDesc(String searchTerm);
    List <NormalizedToken> findTop10ByToken(String token);
    List<NormalizedToken> findTop10ByTokenAndType(String token, TokenType type);


}
