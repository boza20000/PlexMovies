package com.example.movies.repository;

import com.example.movies.model.AwardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AwardRepository extends JpaRepository<AwardEntity, Long> {

    Optional<AwardEntity> findAwardEntitiesByAward(String award);

}
