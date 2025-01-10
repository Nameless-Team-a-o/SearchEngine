package com.nameless.storage_server.service.normalize.splitter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SnakeCaseHandler implements SplittingHandler {
    private SplittingHandler next;

    @Override
    public void setNext(SplittingHandler next) {
        this.next = next;
    }

    @Override
    public List<String> handle(String token) {
        List<String> words = new ArrayList<>();

        String[] parts = token.split("_");

        if(parts.length == 1 && next != null) {
            words.addAll(next.handle(parts[0]));
            return words;
        }
        else{
            String finalWord = parts[0].toLowerCase();
            for (int i = 1 ; i < parts.length ; i++) {
                parts[i] =  capitalize(parts[i]) ;
                finalWord = finalWord + parts[i];
            }
            if (next != null)
                words.addAll(next.handle(finalWord));
        }

        return words;
    }

    private String capitalize(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }
}
