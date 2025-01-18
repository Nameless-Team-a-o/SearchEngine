package com.nameless.storage_server.entity.token;

import com.nameless.storage_server.entity.Clazz;
import com.nameless.storage_server.entity.TokenType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Index;

@Entity
@Table(indexes = {
        @Index(name = "idx_normalize_token", columnList = "token"),
        @Index(name = "idx_normalize_type", columnList = "type")
})
public class NormalizedToken extends Token {

    public NormalizedToken(String token, TokenType type, Long lineNumber, Clazz clazz) {
        super(token, type, lineNumber, clazz);
    }

    public NormalizedToken() {
        super();
    }
}
