package com.ot.client.model.search.tv;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.ot.client.model.search.ResultInterface;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@JsonTypeName("tv")
public class TvShowResult implements ResultInterface {

    @Schema(description = "포스터 경로")
    private String posterPath;

    @Schema(description = "인기도 decimal type")
    private BigDecimal popularity;

    @Schema(description = "tv id")
    private int id;

    @Schema(description = "배경사진")
    private String backdropPath;

    @Schema(description = "평점")
    private BigDecimal voteAverage;

    @Schema(description = "줄거리")
    private String overview;
    @Schema(description = "첫 방영 연도")
    private String firstAirDate;

    @Schema(description = "국가")
    private List<String> originCountry;

    @Schema(description = "장르 아이디 리스트")
    private List<Integer> genreIds;

    @Schema(description = "원어")
    private String originalLanguage;

    @Schema(description = "평점 평가수")
    private int voteCount;

    @Schema(description = "제목")
    private String name;

    @Schema(description = "원어 명")
    private String originalName;

    private String mediaType = "tv";

}
