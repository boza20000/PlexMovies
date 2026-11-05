package com.example.movies.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("MOVIE")
@Table(name = "movie")
@Entity
public class MovieEntity extends MediaItemEntity {

    private String duration;
    private String year;

}