package com.nameless.storage_server.entity.token;

import com.nameless.storage_server.entity.Clazz;
import com.nameless.storage_server.entity.TokenType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@ToString
public abstract class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @Enumerated(EnumType.STRING)
    private TokenType type;

    private Long lineNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private Clazz clazz;

    @CreationTimestamp
    private LocalDateTime createdDate;

    public Token(String token, TokenType type, Long lineNumber, Clazz clazz) {
        this.token = token;
        this.type = type;
        this.lineNumber = lineNumber;
        this.clazz = clazz;
    }
}
