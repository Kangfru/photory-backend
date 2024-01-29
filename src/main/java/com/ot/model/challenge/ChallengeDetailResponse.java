package com.ot.model.challenge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ChallengeDetailResponse {


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

    @Schema(name = "contents", description = "도장깨기 선택 작품")
    private List<ChallengeDetailResponse.Content> contents = new ArrayList<>();

    @Schema(name = "total_count", description = "총 컨텐츠 수")
    private Long totalCount;
    @Schema(name = "done_count", description = "도장깨기완료 컨텐츠 수")
    private Long doneCount;
    @Schema(name = "total_run_time", description = "총 러닝타임 분")
    private Long totalRunTime;
    @Schema(name = "done_run_time", description = "도장깨기완료 러닝타임 분")
    private Long doneRunTime;
    @Schema(name = "last_content_done_date", description = "최근 컨텐츠 도장깨기 완료 일자")
    private String lastContentDoneDate;
    @Schema(name = "member", description = "회원 정보")
    private ChallengeDetailResponse.Member member;
    @Schema(name = "read_count", description = "조회수")
    private Long readCount;

    @Getter
    @Setter
    public static class Content {

        @Schema(name = "challenge_content_id", description = "컨텐츠 id")
        private String challengeContentId;

        @Schema(name = "contents_type", description = "타입 (movie or tv)", allowableValues = {"tv", "movie"})
        private String contentsType;

        @Schema(name = "contents_id", description = "tmdb id")
        private String contentsId;

        @Schema(name = "title", description = "제목")
        private String title;

        @Schema(name = "original_title", description = "original 제목")
        private String originalTitle;

        @Schema(name = "poster_path", description = "posterPath")
        private String posterPath;

        @Schema(name = "backdrop_path", description = "backdropPath")
        private String backdropPath;

        @Schema(name = "is_done", description = "깨기 완료 여부")
        private Boolean isDone;

        @Schema(name = "done_date", description = "깨기 완료 일자")
        private String doneDate;

        @Schema(name = "run_time", description = "러닝타임")
        private Long runTime;

        @Schema(name = "seq", description = "컨텐츠 순서(오름차순)")
        private Integer seq;

        @Schema(name = "photo_ticket", description = "도장깨기 완료시 포토티켓 정보 (미완료시 null)")
        private ChallengeDetailResponse.PhotoTicket photoTicket;

        @Schema(name = "providers", description = "작품 제공업체 정보 리스트")
        private List<ChallengeDetailResponse.Provider> providers;

    }


    @Getter
    @Setter
    public static class PhotoTicket {
        @Schema(name = "photo_ticket_id", description = "포토티켓 id")
        private String photoTicketId;
        @Schema(name = "rating", description = "포토티켓 점수")
        private int rating;
        @Schema(name = "view_date", description = "포토티켓 생성일")
        private Date viewDate;
    }

    @Getter
    @Setter
    public static class Member {
        @Schema(name = "member_seq", description = "회원 id")
        private String memberSeq;
        @Schema(name = "email", description = "이메일")
        private String email;
        @Schema(name = "nick_name", description = "닉네임")
        private String nickName;

    }

    @Getter
    @Setter
    public static class Provider {
        @Schema(name = "type", description = "제공 타입")
        private String type;
        private int displayPriority;
        private String logoPath;
        private String providerName;
        private String providerId;

    }


}
