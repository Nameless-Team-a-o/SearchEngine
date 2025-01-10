package com.nameless.storage_server.service.normlizeToken;

import com.nameless.storage_server.entity.Clazz;
import com.nameless.storage_server.entity.NormalizeToken;
import com.nameless.storage_server.entity.Token;
import com.nameless.storage_server.entity.TokenType;
import com.nameless.storage_server.repository.NormalizeTokenRepository;
import com.nameless.storage_server.service.normalize.strategy.TokenStorageStrategy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NormalizeTokenService {
    private final NormalizeTokenRepository normalizeTokenRepository;
    private final TokenStorageStrategy tokenStorageStrategy;

    public NormalizeTokenService(NormalizeTokenRepository normalizeTokenRepository) {
        this.normalizeTokenRepository = normalizeTokenRepository;
        this.tokenStorageStrategy = new TokenStorageStrategy(this);
    }



    public void saveNormalizeToken(NormalizeToken normalizeToken) {
        normalizeTokenRepository.save(normalizeToken);
    }

    public void saveAllNormalizeToken(List<NormalizeToken> normalizedTokens) {
        normalizeTokenRepository.saveAll(normalizedTokens);
    }



    public void processAndStoreToken(Token token, List<String> normalizedWords) {
        tokenStorageStrategy.storeProcessedToken(
                token.getToken(),
                normalizedWords,
                token
        );
    }


    public List<NormalizeToken> findTop10Token(String searchTerm) {
         return normalizeTokenRepository.findTop10ByToken(searchTerm);
    }
    public List<NormalizeToken> findTop10ByTokenAndType(String token, TokenType type){
        return normalizeTokenRepository.findTop10ByTokenAndType(token, type);
    }

}
