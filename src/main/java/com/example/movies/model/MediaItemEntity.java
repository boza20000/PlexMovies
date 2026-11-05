package com.example.movies.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "item_type")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "media_item")
public class MediaItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT", length = 4000)
    private String description;

    private Integer rating;

    private LocalDate releaseDate;

    @NotNull
    @Column(name = "imdb_id")
    private String imdbid;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "award_media_item",
            joinColumns = @JoinColumn(name = "media_item_id"),
            inverseJoinColumns = @JoinColumn(name = "award_id"))
    private Set<AwardEntity> awards = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "genre_media_item",
            joinColumns = @JoinColumn(name = "media_item_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private Set<GenreEntity> genres = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "language_media_item",
            joinColumns = @JoinColumn(name = "media_item_id"),
            inverseJoinColumns = @JoinColumn(name = "language_id"))
    private Set<LanguageEntity> languages = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "people_media_item",
            joinColumns = @JoinColumn(name = "media_item_id"),
            inverseJoinColumns = @JoinColumn(name = "people_id"))
    private Set<PersonEntity> people = new HashSet<>();

}
