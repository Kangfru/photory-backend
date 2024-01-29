package com.ot.client.model.search.tv;

import com.ot.client.model.common.*;
import com.ot.client.model.search.credits.CreditSearchRes;
import com.ot.client.model.search.genre.GenreResult;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class TvShowDetail extends TvShowResult {

    private String backdropPath;

    private List<Creator> createdBy;

    @Getter
    @Setter
    public static class Creator {
        private int id;

        private String creditId;

        private String name;

        private int gender;

        private String profilePath;
    }

    private int[] episodeRunTime;

    private String firstAirDate;

    private List<GenreResult> genres;

    private String homepage;

    private int id;

    private boolean inProduction;

    private String[] languages;

    private String lastAirDate;

    private EpisodeAir lastEpisodeToAir;

    private String name;

    private EpisodeAir nextEpisodeToAir;

    private List<Network> networks;

    private int numberOfEpisodes;

    private int numberOfSeasons;

    private List<String> originCountry;

    private String originalLanguage;

    private String originalName;

    private String overview;

    private BigDecimal popularity;

    private String posterPath;

    private List<ProductionCompany> productionCompanies;

    private List<ProductionCountry> productionCountries;

    private List<Season> seasons;

    private List<SpokenLanguage> spokenLanguages;

    private String status;

    private String tagLine;

    private String type;

    private BigDecimal voteAverage;

    private int voteCount;

    private String certification;

    private CreditSearchRes credit;

    private String totalRuntime;
    
}
