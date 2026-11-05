package com.example.movies.service.impl;

import com.example.movies.model.LanguageEntity;
import com.example.movies.model.StreamType;
import com.example.movies.repository.LanguageRepository;
import com.example.movies.service.LanguageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class LanguageServiceImpl implements LanguageService {
    private final LanguageRepository languageRepository;

    @Override
    public LanguageEntity createLanguage(String language, StreamType type) {
        Optional<LanguageEntity> awardEntity = languageRepository.findLanguageEntitiesByLanguageIgnoreCaseAndStreamType(language.trim(), type);

        if (awardEntity.isEmpty()) {
            return languageRepository.save(new LanguageEntity(null, language.trim(), type));
        }

        return awardEntity.get();
    }
}
