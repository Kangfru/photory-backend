package com.ot.model.challenge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class SaveChallengeRequest {

    @Size(max = 20)
    @NotBlank
    @Schema(name = "title", description = "도장깨기 제목")
    private String title;

    @Size(max = 30)
    @Schema(name = "description", description = "상세 설명(선택)")
    private String description;

    @Schema(name = "exposed", description = "공개 설정 (true : 전체 공개, false : 나만보기")
    private Boolean exposed;

    @Size(min = 3, max = 48)
    @Schema(name = "contents", description = "도장깨기 선택 작품")
    private List<Content> contents = new ArrayList<>();

    @Getter
    @Setter
    @ToString
    @Schema(name = "content")
    public static class Content {

        @NotBlank
        @Schema(name = "contents_type", description = "타입 (movie or tv)", allowableValues = {"tv", "movie"})
        private String contentsType;

        @NotBlank
        @Schema(name = "contents_id", description = "tmdb id")
        private String contentsId;

        @NotBlank
        @Schema(name = "title", description = "제목")
        private String title;

        @NotBlank
        @Schema(name = "original_title", description = "original 제목")
        private String originalTitle;

        @Schema(name = "poster_path", description = "posterPath")
        private String posterPath;

        @Schema(name = "backdrop_path", description = "backdropPath")
        private String backdropPath;

        @Schema(name = "run_time", description = "러닝타임")
        private Long runTime;
    }

}
