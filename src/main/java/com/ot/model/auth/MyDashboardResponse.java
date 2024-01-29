package com.ot.model.auth;

import com.ot.model.challenge.ChallengeDashboardResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class MyDashboardResponse {

    @Schema(name = "total_challenge_count", description = "총 도장깨기 수")
    private Integer totalChallengeCount = 0;
    @Schema(name = "total_photo_ticket_count", description = "총 포토티켓 수")
    private Integer totalPhotoTicketCount = 0;
    @Schema(name = "tv", description = "총 tv 컨텐츠 정보")
    private ChallengeDashboardResponse.DataDashboard tv = new ChallengeDashboardResponse.DataDashboard();
    @Schema(name = "movie", description = "총 movie 컨텐츠 정보")
    private ChallengeDashboardResponse.DataDashboard movie = new ChallengeDashboardResponse.DataDashboard();
    @Schema(name = "record", description = "제공업체별 컨텐츠 정보")
    private Map<String, ChallengeDashboardResponse.DataDashboard> record = new HashMap<>();

    @Getter
    @Setter
    public static class DataDashboard {
        @Schema(name = "total_count", description = "총 컨텐츠 수")
        private Integer totalCount = 0;
        @Schema(name = "done_count", description = "총 깨기완료 컨텐츠 수")
        private Integer doneCount = 0;
        @Schema(name = "total_run_time", description = "총 컨텐츠 러닝타임")
        private Integer totalRunTime = 0;
        @Schema(name = "done_run_time", description = "총 깨기완료 컨텐츠 러닝타임")
        private Integer doneRunTime = 0;
    }

}
