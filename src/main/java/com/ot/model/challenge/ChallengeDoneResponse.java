package com.ot.model.challenge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChallengeDoneResponse {

    @Schema(name = "total_done_count", description = "현재까지 도장깨기완료 횟수")
    private Integer totalDoneCount;
    @Schema(name = "challenge_list", description = "조회시 완료된 도장깨기 리스트")
    private List<Challenge> challengeList;

    @Getter
    @Setter
    public static class Challenge {
        @Schema(name = "challenge_id", description = "도장깨기 id")
        private String challengeId;

        @Schema(name = "title", description = "도장깨기 제목")
        private String title;

        @Schema(name = "description", description = "상세 설명(선택)")
        private String description;

        @Schema(name = "exposed", description = "공개 설정 (true : 전체 공개, false : 나만보기")
        private Boolean exposed;

        @Schema(name = "status", description = "상태")
        private String status;

        @Schema(name = "done_date", description = "도장깨기 완료 일자")
        private String doneDate;

        @Schema(name = "doneCount", description = "도장깨기완료 컨텐츠 수")
        private Long doneCount;
        @Schema(name = "doneRunTime", description = "도장깨기완료 러닝타임 분")
        private Long doneRunTime;
    }

}
