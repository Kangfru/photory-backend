package com.ot.model.challenge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class ModifyChallengeContentOrderRequest {

    @NotBlank
    @Schema(name = "challenge_content_id", description = "도장깨기 컨텐츠 sequence")
    private String challengeContentId;

    @NotNull
    @Min(0)
    @Schema(name = "order", description = "순서 (0 ~ max)")
    private Integer order;

}
