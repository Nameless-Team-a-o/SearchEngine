package com.nameless.storage_server.repository;

import com.nameless.storage_server.entity.Token;
import com.nameless.storage_server.entity.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TokenRepository  extends JpaRepository<Token, Long> {


    List<Token> findTop10ByToken(String searchTerm);
    List<Token> findTop10ByTokenAndType(String token, TokenType type);
}