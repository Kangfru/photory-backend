package com.ot.model.challenge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class ModifyChallengeRequest {

    @NotBlank
    @Schema(name = "title", description = "도장깨기 제목")
    private String title;

    @Schema(name = "description", description = "상세 설명(선택)")
    private String description;

    @Schema(name = "exposed", description = "공개 설정 (true : 전체 공개, false : 나만보기")
    private Boolean exposed;

}
