package com.nameless.storage_server.entity.token;

import com.nameless.storage_server.entity.Clazz;
import com.nameless.storage_server.entity.TokenType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Index;

@Entity
@Table(name = "token", indexes = {
        @Index(name = "idx_token_token", columnList = "token"),
        @Index(name = "idx_token_type", columnList = "type")
})
public class OriginalToken extends Token {

    public OriginalToken(String token, TokenType type, Long lineNumber, Clazz clazz) {
        super(token, type, lineNumber, clazz);
    }

    public OriginalToken() {
    }
}
