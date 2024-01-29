package com.ot.client.model.common;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class EpisodeAir {

    private String airDate;

    private int episodeNumber;

    private int id;

    private String name;

    private String overview;

    private String productionCode;

    private int seasonNumber;

    private String stillPath;

    private BigDecimal voteAverage;

    private int voteCount;

    private int runtime;

}
