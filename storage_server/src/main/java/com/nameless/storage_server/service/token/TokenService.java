package com.nameless.storage_server.service.token;

import com.nameless.storage_server.entity.Clazz;
import com.nameless.storage_server.entity.Token;
import com.nameless.storage_server.entity.TokenType;
import com.nameless.storage_server.repository.TokenRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TokenService {
    private final TokenRepository tokenRepository;
    public TokenService(final TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public Token createToken(String nameAsString, TokenType tokenType, long line, Clazz clazz) {
        return saveToken(new Token(nameAsString, tokenType, line, clazz));
    }

    public Token saveToken(Token token) {
        return tokenRepository.save(token);
    }

    public List<Token> findTop10Token(String searchTerm) {
        return tokenRepository.findTop10ByToken(searchTerm);
    }
    public List<Token> findTop10ByTokenAndType(String token, TokenType type){
        return tokenRepository.findTop10ByTokenAndType(token, type);
    }

}
