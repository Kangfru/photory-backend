package com.ot.client.model.search.person;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class KnownFor {
    private String mediaType;

    private List<Integer> genreIds;

    private boolean adult;

    private String backdropPath;

    private int id;

    @JsonAlias({ "title", "name" })
    private String title;

    private String originalLanguage;

    @JsonAlias({ "original_title", "original_name" })
    private String originalTitle;

    private String overview;

    private String posterPath;

    private BigDecimal popularity;

    private String releaseDate;

    private boolean video;

    private BigDecimal voteAverage;

    private Integer voteCount;

}
