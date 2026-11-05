package com.example.movies.service.impl;

import com.example.movies.model.PersonEntity;
import com.example.movies.model.PersonKey;
import com.example.movies.model.Role;
import com.example.movies.repository.PersonRepository;
import com.example.movies.service.PersonService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
public class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepository;
    private final ConcurrentHashMap<PersonKey, PersonEntity> peopleCache = new ConcurrentHashMap<>();

    @Override
    public PersonEntity createPerson(String name, Role personRole) {
        PersonKey key = new PersonKey(name.trim().toLowerCase(), personRole);

        return peopleCache.computeIfAbsent(key, k ->
                personRepository.findByNameAndRole(name.trim(), personRole)
                        .orElseGet(() ->
                                personRepository.save(new PersonEntity(null, name.trim(), personRole))));
    }
}