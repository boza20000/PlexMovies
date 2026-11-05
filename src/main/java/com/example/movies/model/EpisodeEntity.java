package com.example.movies.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("EPISODE")
@Table(name = "episode")
public class EpisodeEntity extends MediaItemEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id")
    private SeasonEntity season;

    @NotNull
    @Min(1)
    private Integer episodeNumber;

    @NotNull
    @Min(1)
    private Integer duration;
}
