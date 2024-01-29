package com.ot.controller;

import com.ot.client.model.search.credits.CreditSearchRes;
import com.ot.client.model.search.genre.GenreSearchRes;
import com.ot.client.model.search.movie.MovieDetail;
import com.ot.client.model.search.movie.MovieSearchRes;
import com.ot.client.model.search.multi.MultiRuntimeReq;
import com.ot.client.model.search.multi.MultiRuntimeRes;
import com.ot.client.model.search.multi.MultiSearchGenreRes;
import com.ot.client.model.search.multi.MultiSearchRes;
import com.ot.client.model.search.person.PersonDetail;
import com.ot.client.model.search.person.PersonSearchRes;
import com.ot.client.model.search.provider.ContentsCertificationSearchRes;
import com.ot.client.model.search.provider.ContentsProviderSearchRes;
import com.ot.client.model.search.tv.TvSearchRes;
import com.ot.client.model.search.tv.TvShowDetail;
import com.ot.config.AppConfig;
import com.ot.exception.ServiceException;
import com.ot.model.challenge.PageChallengeResponse;
import com.ot.model.search.ContentsCommentsRequest;
import com.ot.model.search.ContentsCommentsResponse;
import com.ot.model.search.MediaSearchResponse;
import com.ot.service.ChallengeService;
import com.ot.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {

    private final AppConfig appConfig;

    private final SearchService searchService;
    private final ChallengeService challengeService;

    @GetMapping(value = "/", produces = "application/json")
    @ResponseBody
    @Operation(summary = "get multi search results by query", description = "전체 검색",
            parameters = {
                    @Parameter(name = "query", description = "검색어"),
                    @Parameter(name = "page", description = "페이지")
            }
    )
    public MultiSearchGenreRes getMultiSearchResults(@RequestParam String query, @RequestParam int page,
                                                     @RequestParam(required = false) String region) {
        return searchService.getMultiSearchResults(query, page, region);
    }

    @GetMapping(value = "/movies", produces = "application/json")
    @ResponseBody
    @Operation(summary = "get movie list by query", description = "영화 검색",
            parameters = {
                    @Parameter(name = "query", description = "검색어"),
                    @Parameter(name = "page", description = "페이지 번호")
            }
    )
    public MovieSearchRes getMovies(@RequestParam String query, @RequestParam int page) {
        return searchService.getMovieListByQuery(query, page);
    }

    @GetMapping(value = "/movies/{movieId}")
    @ResponseBody
    @Operation(summary = "get movie details by movie id", description = "영화 디테일 가져오기",
            parameters = {
                    @Parameter(name = "movieId", description = "tmdb id 번호", required = true, in = ParameterIn.PATH)
            }
    )
    public MovieDetail getMovieDetails(@PathVariable String movieId) {
        return searchService.getMovieDetail(movieId);
    }

    @GetMapping("/credits")
    @ResponseBody
    @Operation(summary = "get movie or tv credits", description = "영화 및 TV 쇼 제작진 검색",
            parameters = {
                    @Parameter(name = "contentsType", description = "type (movie or tv)"),
                    @Parameter(name = "contentsId", description = "각 컨텐츠 id")
            }
    )
    public CreditSearchRes getCredits(@RequestParam String contentsType, @RequestParam String contentsId) throws Exception {
        return searchService.getCredits(contentsType, contentsId);
    }


    @GetMapping(value = "/tv-shows", produces = "application/json")
    @ResponseBody
    @Operation(summary = "get tv show list by query", description = "TV show 검색",
            parameters = {
                    @Parameter(name = "query", description = "검색어"),
                    @Parameter(name = "page", description = "페이지 번호"),
                    @Parameter(name = "firstAirDateYear", description = "첫 방영 연도", required = false)
            }
    )
    public TvSearchRes getTvShows(@RequestParam String query,
                                  @RequestParam int page,
                                  @RequestParam(required = false) String firstAirDateYear) {
        return searchService.getTvByQuery(query, page, firstAirDateYear);
    }

    @GetMapping(value = "/tv-shows/{tvId}", produces = "application/json")
    @ResponseBody
    @Operation(summary = "get tv show detail", description = "TV detail",
            parameters = {
                    @Parameter(name = "tvId", description = "tmdb id 번호", required = true, in = ParameterIn.PATH)
            }
    )
    public TvShowDetail getTvShowResult(@PathVariable String tvId) {
        return searchService.getTvDetail(tvId);
    }

    @GetMapping(value = "/people", produces = "application/json")
    @ResponseBody
    @Operation(summary = "get people by query", description = "배우, 감독 등 검색",
            parameters = {
                    @Parameter(name = "query", description = "검색어"),
                    @Parameter(name = "page", description = "페이지 번호")
            }
    )
    public PersonSearchRes getPeople(@RequestParam String query, @RequestParam int page) {

        return searchService.getPeopleByQuery(query, page);
    }

    @GetMapping(value = "/people/{personId}")
    @ResponseBody
    @Operation(summary = "get person details by person id", description = "배우, 감독 등 디테일 가져오기",
            parameters = {
                    @Parameter(name = "personId", description = "person id 번호")
            }
    )
    public PersonDetail getPersonDetail(@PathVariable String personId) {
        return searchService.getPersonDetail(personId);
    }

    /**
     * 장르
     */
    @GetMapping(value = "/genres", produces = "application/json")
    @ResponseBody
    @Operation(summary = "get genres", description = "영화 장르 리스트 가져오기 (contentsType : movie or tv)",
            parameters = {
                    @Parameter(name = "contentsType", description = "movie or tv", in = ParameterIn.QUERY, required = true, schema = @Schema(allowableValues = {"tv", "movie"}))
            }
    )
    public GenreSearchRes getGenres(@RequestParam String contentsType) {
        if (!"tv".equals(contentsType) && !"movie".equals(contentsType)) {
            throw new ServiceException("500", "PARAMETER를 확인하세요.", HttpStatus.BAD_REQUEST);
        }
        return searchService.getGenresByType(contentsType);
    }

    @GetMapping(value = "/providers", produces = "application/json")
    @ResponseBody
    @Operation(summary = "get provider by movie id", description = "contents id, type 으로 컨텐츠 제공자 가져오기",
            parameters = {
                    @Parameter(name = "contentsId", description = "각 movie id or tv id", in = ParameterIn.QUERY),
                    @Parameter(name = "contentsType", description = "(type : movie or tv)", in = ParameterIn.QUERY, schema = @Schema(allowableValues = {"tv", "movie"}))
            }
    )
    public ContentsProviderSearchRes getProviderByMovieId(@RequestParam String contentsId, @RequestParam String contentsType) {
        return searchService.getProviderByContentsId(contentsId, contentsType);
    }

    @GetMapping(value = "/certifications", produces = "application/json")
    @ResponseBody
    @Operation(summary = "get certification", description = "contents id, type 으로 컨텐츠 연령 가져오기",
            parameters = {
                    @Parameter(name = "contentsId", description = "각 movie id or tv id", in = ParameterIn.QUERY),
                    @Parameter(name = "contentsType", description = "(type : movie or tv)", in = ParameterIn.QUERY, schema = @Schema(allowableValues = {"tv", "movie"}))
            }
    )
    public ContentsCertificationSearchRes getContentsCertification(@RequestParam String contentsId, @RequestParam String contentsType) throws Exception {
        return searchService.getContentsCertification(contentsId, contentsType);
    }

    @PostMapping(value = "/comments")
    @ResponseBody
    @Operation(summary = "get comments", description = "컨텐츠 별 한줄 평 가져오기")
    public ContentsCommentsResponse getContentsComments(@RequestBody ContentsCommentsRequest contentsCommentsRequest) throws Exception {
        return searchService.getContentsComments(contentsCommentsRequest);
    }

    @GetMapping(value = "/recommend-challenge")
    @Operation(summary = "contents id, type 으로 추천 도장깨기 리스트 (본인거 제외)", description = "", parameters = {
            @Parameter(name = "pageNumber", description = "불러올 페이지 번호(0 ~ total_pages-1)", in = ParameterIn.QUERY, required = true),
            @Parameter(name = "size", description = "페이지당 최대 size(기본:20, 전체:-1)", in = ParameterIn.QUERY),
            @Parameter(name = "contentsId", description = "각 movie id or tv id", in = ParameterIn.QUERY),
            @Parameter(name = "contentsType", description = "(type : movie or tv)", in = ParameterIn.QUERY, schema = @Schema(allowableValues = {"tv", "movie"}))
    })
    public PageChallengeResponse getRecommendChallengeList(
            @RequestHeader Map<String, String> header,
            @RequestParam @NotNull @NotEmpty Integer pageNumber,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam String contentsId, @RequestParam String contentsType) {
        return challengeService.getRecommendChallengeList(header, pageNumber, size, contentsId, contentsType);
    }

    @GetMapping(value = "/exist-content")
    @Operation(summary = "contents id, type 으로 내 도장깨기 컨텐츠 리스트에 존재 여부 있으면 도장깨기 정보", description = "",
            parameters = {
                    @Parameter(name = "contentsId", description = "각 movie id or tv id", in = ParameterIn.QUERY),
                    @Parameter(name = "contentsType", description = "(type : movie or tv)", in = ParameterIn.QUERY, schema = @Schema(allowableValues = {"tv", "movie"}))
            }
    )
    public Boolean checkExistChallenge(@RequestParam String contentsId, @RequestParam String contentsType) throws Exception {
        return challengeService.checkExistChallenge(contentsId, contentsType);
    }

    @GetMapping(value = "/trending")
    @Operation(summary = "트렌딩 가져오기 (6건까지) day 기준으로 변경")
    public MultiSearchRes getTrendingList(@RequestHeader Map<String, String> header) {
        return searchService.getTrendingList();
    }

//    @GetMapping(value = "/tv-shows/{tvId}/season/{seasonNumber}", produces = "application/json")
//    @ResponseBody
//    @Operation(summary = "get tv season detail", description = "TV show season detail",
//            parameters = {
//                    @Parameter(name = "tvId", description = "tmdb id 번호", required = true, in = ParameterIn.PATH),
//                    @Parameter(name = "seasonNumber", description = "season number", required = true, in = ParameterIn.PATH)
//            }
//    )
//    public TvSeasonDetail getTvSeason(@RequestHeader Map<String, String> header,
//                                      @PathVariable String tvId,
//                                      @PathVariable String seasonNumber) throws Exception {
//        return searchService.getTvSeasonDetail(tvId, seasonNumber);
//    }

    @GetMapping(value = "/tv-shows/{tvId}/season/{seasonNumber}/credits", produces = "application/json")
    @ResponseBody
    @Operation(summary = "get tv season detail", description = "TV show season detail",
            parameters = {
                    @Parameter(name = "tvId", description = "tmdb id 번호", required = true, in = ParameterIn.PATH),
                    @Parameter(name = "seasonNumber", description = "season number", required = true, in = ParameterIn.PATH)
            }
    )
    public CreditSearchRes getTvSeasonCredits(@RequestHeader Map<String, String> header,
                                             @PathVariable String tvId,
                                             @PathVariable String seasonNumber) throws Exception {
        return searchService.getTvSeasonCredits(tvId, seasonNumber);
    }

    @GetMapping(value = "/media", produces = "application/json")
    @ResponseBody
    @Operation(summary = "도장 깨기용 tv, movie 검색 api", description = "전체 검색 only media",
            parameters = {
                    @Parameter(name = "query", description = "검색어"),
                    @Parameter(name = "page", description = "페이지")
            }
    )
    public MediaSearchResponse getMediaSearchResponse(@RequestParam String query, @RequestParam int page,
                                                     @RequestParam(required = false) String region) {
        return searchService.getMediaSearchResults(query, page, region);
    }

    @PostMapping(value = "/runningtime", produces = "application/json", consumes = "application/json")
    @ResponseBody
    @Operation(summary = "running time 배열로 가져오기 api", description = "러닝타임 가져오기")
    public MultiRuntimeRes getRuntimes(@RequestBody MultiRuntimeReq multiRuntimeReq) throws Exception {
        return searchService.getRuntimes(multiRuntimeReq);
    }
}
