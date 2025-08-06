package com.nameless.storage_server.service.normalize.steps;

import com.nameless.storage_server.service.normalize.splitter.TokenSplitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.tartarus.snowball.ext.EnglishStemmer;

import java.util.ArrayList;
import java.util.List;

@Component
@Order(1)
public class StemmingStep implements NormalizationStep {
    @Override
    public String normalize(String word) {
        EnglishStemmer stemmer = new EnglishStemmer();
        stemmer.setCurrent(word);

        return stemmer.stem() ? stemmer.getCurrent() : word;
    }
}
