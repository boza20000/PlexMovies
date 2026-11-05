package com.example.movies.service;

import com.example.movies.model.PersonEntity;
import com.example.movies.model.Role;

public interface PersonService {
    PersonEntity createPerson(String person, Role personRole);

}
