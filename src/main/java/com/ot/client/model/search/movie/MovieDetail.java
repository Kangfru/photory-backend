package com.ot.client.model.search.movie;

import com.ot.client.model.common.ProductionCompany;
import com.ot.client.model.common.ProductionCountry;
import com.ot.client.model.common.SpokenLanguage;
import com.ot.client.model.search.credits.CreditSearchRes;
import com.ot.client.model.search.genre.GenreResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class MovieDetail extends MovieResult {

    private String belongToCollection;

    private int budget;

    private List<GenreResult> genres;

    private String homepage;

    private String imdbId;

    private List<ProductionCompany> productionCompanies;

    private List<ProductionCountry> productionCountries;

    private List<SpokenLanguage> spokenLanguages;

    // Allowed Values: Rumored, Planned, In Production, Post Production, Released, Canceled
    private String status;

    private String tagline;

    // 연령정보
    @Schema(description = "연령정보")
    private String certification;

    @Schema(description = "제작진")
    private CreditSearchRes credit;

    @Schema(description = "상영시간")
    private int runtime;

}
