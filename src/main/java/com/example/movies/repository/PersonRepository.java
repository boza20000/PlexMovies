package com.example.movies.repository;

import com.example.movies.model.PersonEntity;
import com.example.movies.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<PersonEntity, Long> {

    Optional<PersonEntity> findByNameAndRole(String name, Role role);
}
