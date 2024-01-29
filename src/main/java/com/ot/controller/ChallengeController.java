package com.ot.controller;

import com.ot.model.challenge.*;
import com.ot.service.ChallengeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/challenge")
public class ChallengeController {


    private final ChallengeService challengeService;

    @GetMapping("/")
    @Operation(summary = "도장깨기 리스트 조회", description = "", parameters = {
            @Parameter(name = "pageNumber", description = "불러올 페이지 번호(0 ~ total_pages-1)", in = ParameterIn.QUERY, required = true),
            @Parameter(name = "size", description = "페이지당 최대 size(기본:20, 전체:-1)", in = ParameterIn.QUERY),
            @Parameter(name = "status", description = "상태", in = ParameterIn.QUERY, schema = @Schema(allowableValues = {"DONE", "ING", ""}))
    })
    public PageChallengeResponse getChallengeList(@RequestHeader Map<String, String> header,
                                                  @RequestParam @NotNull @NotEmpty Integer pageNumber,
                                                  @RequestParam(defaultValue = "20") Integer size,
                                                  @RequestParam(defaultValue = "") String status) {
        return challengeService.getChallengeList(header, pageNumber, size, status);
    }

    @GetMapping("/status")
    @Operation(summary = "포토티켓 생성,제거 또는 도장깨기 컨텐츠 등록,제거 후 도장깨기판 완성 노출 여부", description = "응답데이터중 challenge_list 존재할 경우 완성 노출 팝업 뜨면 됨.")
    public ChallengeDoneResponse getChallengeDone(@RequestHeader Map<String, String> header) {
        return challengeService.getChallengeDone(header);
    }

    @GetMapping("/dashboard")
    @Operation(summary = "내정보 > 도장깨기 현황", description = "")
    public ChallengeDashboardResponse getDashboard(@RequestHeader Map<String, String> header) {
        return challengeService.getDashboard(header);
    }

    @GetMapping("/{id}")
    @Operation(summary = "도장깨기 상세 조회", description = "", parameters = {
            @Parameter(name = "id", description = "challengeId", in = ParameterIn.PATH),
            @Parameter(name = "isIncreaseReadCount", description = "조회수 증가 여부", in = ParameterIn.QUERY)
    })
    public ChallengeDetailResponse getChallenge(@RequestHeader Map<String, String> header
            , @PathVariable String id, @RequestParam(defaultValue = "true") boolean isIncreaseReadCount) {
        return challengeService.getChallenge(header, id, isIncreaseReadCount);
    }

    @PostMapping("/")
    @Operation(summary = "도장깨기 등록", description = "")
    public String saveChallenge(@RequestHeader Map<String, String> header
            , @RequestBody @Validated SaveChallengeRequest request) {
        return challengeService.saveChallenge(header, request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "도장깨기 수정", description = "", parameters = {
            @Parameter(name = "id", description = "challengeId", in = ParameterIn.PATH)
    })
    public String modifyChallenge(@RequestHeader Map<String, String> header
            , @PathVariable String id, @RequestBody @Validated ModifyChallengeRequest request) {
        return challengeService.modifyChallenge(header, id, request);
    }

    @PutMapping("/remove")
    @Operation(summary = "도장깨기 삭제", description = "ids : challenge_content_id 리스트")
    public List<String> removeChallenge(@RequestHeader Map<String, String> header
            , @RequestBody @Validated RemoveChallengeRequest request) {
        return challengeService.removeChallengeList(header, request);
    }

    @PostMapping("/{id}/content")
    @Operation(summary = "도장깨기 컨텐츠 등록", description = "", parameters = {
            @Parameter(name = "id", description = "challengeId", in = ParameterIn.PATH)
    })
    public String addChallengeContent(@RequestHeader Map<String, String> header
            , @PathVariable String id, @RequestBody @Validated SaveChallengeRequest.Content content) {
        return challengeService.addChallengeContent(header, id, content);
    }

    @DeleteMapping("/{id}/content/{contentId}")
    @Operation(summary = "도장깨기 컨텐츠 삭제", description = "", parameters = {
            @Parameter(name = "id", description = "challengeId", in = ParameterIn.PATH),
            @Parameter(name = "contentId", description = "challengeContentId", in = ParameterIn.PATH)
    })
    public String removeChallengeContent(@RequestHeader Map<String, String> header
            , @PathVariable String id, @PathVariable String contentId) {
        return challengeService.removeChallengeContent(header, id, contentId);
    }

    @PutMapping("/{id}/content/{contentId}/seq")
    @Operation(summary = "도장깨기 컨텐츠 순서 변경", description = "도장깨기 조회 정보 리턴", parameters = {
            @Parameter(name = "id", description = "challengeId", in = ParameterIn.PATH),
            @Parameter(name = "contentId", description = "challengeContentId", in = ParameterIn.PATH),
            @Parameter(name = "seq", description = "새로운 순서값(0 ~ maxLength)", in = ParameterIn.QUERY)
    })
    public ChallengeDetailResponse saveSeqChallengeContent(@RequestHeader Map<String, String> header,
                                                           @PathVariable String id,
                                                           @PathVariable String contentId,
                                                           @RequestParam Integer seq) {
        return challengeService.saveSeqChallengeContent(header, id, contentId, seq);
    }

}
