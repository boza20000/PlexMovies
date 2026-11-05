package com.example.movies.service;

import com.example.movies.model.LanguageEntity;
import com.example.movies.model.StreamType;

public interface LanguageService {
    LanguageEntity createLanguage(String language, StreamType type);
}
