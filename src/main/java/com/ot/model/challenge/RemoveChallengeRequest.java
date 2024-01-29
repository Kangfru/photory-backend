package com.ot.model.challenge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
@ToString
public class RemoveChallengeRequest {

    @NotEmpty
    @Schema(name = "ids", description = "도장깨기 id 리스트")
    private List<String> ids;

}
