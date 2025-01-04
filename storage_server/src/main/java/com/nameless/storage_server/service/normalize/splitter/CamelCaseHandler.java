package com.nameless.storage_server.service.normalize.splitter;


import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class CamelCaseHandler implements SplittingHandler {
    private SplittingHandler next;

    @Override
    public void setNext(SplittingHandler next) {
        this.next = next;
    }

    @Override
    public List<String> handle(String token) {
        List<String> result = new ArrayList<>();
        StringBuilder word = new StringBuilder();

        for (char c : token.toCharArray()) {
            if (Character.isUpperCase(c)) {
                if (!word.isEmpty()) {
                    result.add(word.toString().toLowerCase());
                }
                word = new StringBuilder().append(c);
            } else {
                word.append(c);
            }
        }
        if (!word.isEmpty()) {
            result.add(word.toString().toLowerCase());
        }
        if (next != null) {
            result.addAll(next.handle(String.join("", result)));
        }
        return result;
    }
}
