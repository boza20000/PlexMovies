package com.example.movies.service.impl;

import com.example.movies.model.AwardEntity;
import com.example.movies.repository.AwardRepository;
import com.example.movies.service.AwardService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AwardServiceImpl implements AwardService {
    private final AwardRepository awardRepository;

    @Override
    public AwardEntity createAward(String award) {
        Optional<AwardEntity> awardEntity = awardRepository.findAwardEntitiesByAward(award);

        if (awardEntity.isEmpty()) {
            return awardRepository.save(new AwardEntity(null, award));
        }

        return awardEntity.get();
    }
}
