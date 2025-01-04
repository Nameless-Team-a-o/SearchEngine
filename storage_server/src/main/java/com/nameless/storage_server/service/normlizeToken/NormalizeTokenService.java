package com.nameless.storage_server.service.normlizeToken;

import com.nameless.storage_server.entity.Clazz;
import com.nameless.storage_server.entity.NormalizeToken;
import com.nameless.storage_server.entity.TokenType;
import com.nameless.storage_server.repository.NormalizeTokenRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NormalizeTokenService {
    private final NormalizeTokenRepository normalizeTokenRepository;
    public NormalizeTokenService(NormalizeTokenRepository normalizeTokenRepository) {
        this.normalizeTokenRepository = normalizeTokenRepository;
    }


    public NormalizeToken createNormalizeToken(String token, TokenType type, Long lineNumber, Clazz clazz) {
        return saveNormalizeToken( new NormalizeToken(token, type, lineNumber, clazz) );
    }

    public NormalizeToken saveNormalizeToken(NormalizeToken normalizeToken) {
        return normalizeTokenRepository.save(normalizeToken);
    }

    public void saveAllNormalizeToken(List<NormalizeToken> normalizedTokens) {
        normalizeTokenRepository.saveAll(normalizedTokens);
    }
}
