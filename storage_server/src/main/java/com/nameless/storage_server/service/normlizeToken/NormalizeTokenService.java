package com.nameless.storage_server.service.normlizeToken;

import com.nameless.storage_server.entity.token.NormalizedToken;
import com.nameless.storage_server.entity.token.OriginalToken;
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



    public void saveNormalizeToken(NormalizedToken normalizeToken) {
        normalizeTokenRepository.save(normalizeToken);
    }

    public void saveAllNormalizeToken(List<NormalizedToken> normalizedTokens) {
        normalizeTokenRepository.saveAll(normalizedTokens);
    }



    public void processAndStoreToken(OriginalToken token, List<String> normalizedWords) {
        tokenStorageStrategy.storeProcessedToken(
                token.getToken(),
                normalizedWords,
                token
        );
    }


    public List<NormalizedToken> findTop10Token(String searchTerm) {
         return normalizeTokenRepository.findTop10ByToken(searchTerm);
    }
    public List<NormalizedToken> findTop10ByTokenAndType(String token, TokenType type){
        return normalizeTokenRepository.findTop10ByTokenAndType(token, type);
    }

}
