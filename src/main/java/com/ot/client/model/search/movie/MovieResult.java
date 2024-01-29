package com.ot.client.model.search.movie;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.ot.client.model.search.ResultInterface;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@JsonTypeName("movie")
public class MovieResult implements ResultInterface {


    @Schema(description = "포스터 경로")
    private String posterPath;
    @Schema(description = "성인물 여부")
    private boolean adult;

    @Schema(description = "줄거리")
    private String overview;

    @Schema(description = "발매일자")
    private String releaseDate;

    @Schema(description = "장르 아이디 리스트")
    private List<Integer> genreIds;

    @Schema(description = "movie id")
    private int id;

    @Schema(description = "원제목")
    private String originalTitle;

    @Schema(description = "원어")
    private String originalLanguage;

    @Schema(description = "제목")
    private String title;

    @Schema(description = "배경 사진")
    private String backdropPath;

    @Schema(description = "인기도")
    private BigDecimal popularity;

    @Schema(description = "포스터 경로")
    private int voteCount;

    private boolean video;

    @Schema(description = "평점")
    private BigDecimal voteAverage;

    private String mediaType = "movie";

}
