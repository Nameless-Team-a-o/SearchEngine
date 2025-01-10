package com.nameless.storage_server.service.normalize.splitter;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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

        Pattern pattern = Pattern.compile("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])");
        String[] words = pattern.split(token);

        for (String word : words) {
            result.add(word.toLowerCase());
        }

        if (next != null) {
            result.addAll(next.handle(String.join(" ", result)));
        }

        return result;
    }
}
