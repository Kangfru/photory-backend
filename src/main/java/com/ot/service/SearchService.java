package com.ot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ot.client.ApiClient;
import com.ot.client.code.ContentsType;
import com.ot.client.code.SearchUri;
import com.ot.client.model.common.Crew;
import com.ot.client.model.search.ResultInterface;
import com.ot.client.model.search.credits.CreditSearchReq;
import com.ot.client.model.search.credits.CreditSearchRes;
import com.ot.client.model.search.credits.GroupedCrew;
import com.ot.client.model.search.genre.GenreSearchReq;
import com.ot.client.model.search.genre.GenreSearchRes;
import com.ot.client.model.search.movie.*;
import com.ot.client.model.search.multi.*;
import com.ot.client.model.search.person.*;
import com.ot.client.model.search.provider.ContentsCertificationSearchRes;
import com.ot.client.model.search.provider.ContentsProviderSearchRes;
import com.ot.client.model.search.provider.ProviderSearchReq;
import com.ot.client.model.search.provider.ProviderSearchRes;
import com.ot.client.model.search.tv.*;
import com.ot.config.AppConfig;
import com.ot.exception.ServiceException;
import com.ot.model.common.Pagination;
import com.ot.model.search.ContentsCommentsRequest;
import com.ot.model.search.ContentsCommentsResponse;
import com.ot.model.search.ContentsSummary;
import com.ot.model.search.MediaSearchResponse;
import com.ot.repository.contents.GenreRepository;
import com.ot.repository.contents.ProviderRepository;
import com.ot.repository.contents.entity.Genre;
import com.ot.repository.contents.entity.Provider;
import com.ot.repository.photo_tikcet.PhotoTicketRepository;
import com.ot.repository.photo_tikcet.entity.PhotoTicket;
import com.ot.util.MultiValueMapConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {
    private final ProviderRepository providerRepository;

    private final AppConfig appConfig;

    private final ApiClient apiClient;

    private final ObjectMapper mainObjectMapper;

    private final GenreRepository genreRepository;

    private final PhotoTicketRepository photoTicketRepository;

    public GenreSearchRes getGenresByType(String type) {
        if (!(type.equals("tv") || type.equals("movie"))) {
            throw new ServiceException("SEARCH01", "Invalid Request Parameter", HttpStatus.BAD_REQUEST);
        }
        GenreSearchReq genreSearchReq = GenreSearchReq.builder()
                .apiKey(appConfig.getTmApi().getApiKey())
                .language("ko")
                .build();

        Map<String, Object> uriParams = new HashMap<>();
        uriParams.put("type", type);

        GenreSearchRes response = apiClient.get(appConfig.getTmApi().getHost() + SearchUri.GENRE_SEARCH.getUri(), null, GenreSearchRes.class,
                MultiValueMapConverter.convert(mainObjectMapper, genreSearchReq), uriParams);

        response.getGenres().forEach(genreResult -> {
            Genre genre = Genre.builder()
                    .genreType(type)
                    .genreName(genreResult.getName())
                    .genreId(String.valueOf(genreResult.getId()))
                    .build();

            genreRepository.save(genre);
        });

        return response;
    }

    public MovieSearchRes getMovieListByQuery(String query, int page) {
        MovieSearchReq movieSearchReq = MovieSearchReq.builder()
                .query(query)
                .page(page)
                .apiKey(appConfig.getTmApi().getApiKey())
                .region("ko")
                .language("ko")
                .build();

        MovieSearchRes response = apiClient.get(appConfig.getTmApi().getHost() + SearchUri.MOVIE_SEARCH.getUri(), null, MovieSearchRes.class,
                MultiValueMapConverter.convert(mainObjectMapper, movieSearchReq));


        return response;
    }

    public PersonSearchRes getPeopleByQuery(String query, int page) {
        PersonSearchReq personSearchReq = PersonSearchReq.builder()
                .query(query)
                .page(page)
                .apiKey(appConfig.getTmApi().getApiKey())
                .region("ko")
                .language("ko")
                .includeAdult(true)
                .build();

        PersonSearchRes personSearchRes = apiClient.get(appConfig.getTmApi().getHost() + SearchUri.PERSON_SEARCH.getUri(), null, PersonSearchRes.class,
                MultiValueMapConverter.convert(mainObjectMapper, personSearchReq));

        return personSearchRes;
    }

    public TvSearchRes getTvByQuery(String query, int page, String firstAirDateYear) {
        TvSearchReq tvSearchReq = TvSearchReq.builder()
                .query(query)
                .page(page)
                .apiKey(appConfig.getTmApi().getApiKey())
                .language("ko")
                .firstAirDateYear(firstAirDateYear)
                .build();

        TvSearchRes tvSearchRes = apiClient.get(appConfig.getTmApi().getHost() + SearchUri.TV_SHOW_SEARCH.getUri(), null, TvSearchRes.class,
                MultiValueMapConverter.convert(mainObjectMapper, tvSearchReq));

        return tvSearchRes;
    }

    public MultiSearchGenreRes getMultiSearchResults(String query, int page, String region) {
        MultiSearchReq multiSearchReq = MultiSearchReq.builder()
                .query(query)
                .page(page)
                .apiKey(appConfig.getTmApi().getApiKey())
                .language("ko")
                .region(region)
                .build();

        MultiSearchRes multiSearchRes = apiClient.get(appConfig.getTmApi().getHost() + SearchUri.MULTI_SEARCH.getUri(), null, MultiSearchRes.class,
                MultiValueMapConverter.convert(mainObjectMapper, multiSearchReq));
        // 장르별 & 타입별 묶기
        List<ResultInterface> movieResultList = new ArrayList<>();
        List<ResultInterface> tvShowResultList = new ArrayList<>();
        List<PersonResult> personResultList = new ArrayList<>();

        for (ResultInterface result : multiSearchRes.getResults()) {
            if (result.getMediaType().equals("movie")) {
                movieResultList.add(result);
            } else if (result.getMediaType().equals("tv")) {
                tvShowResultList.add(result);
            } else if (result.getMediaType().equals("person")) {
                personResultList.add((PersonResult) result);
            }
        }

//        Map<String, Genre> tvAndMovieGenreVoMap = collectByGenreId(resultList);
//        List<Genre> results = new ArrayList<>(tvAndMovieGenreVoMap.values());

        MultiSearchGenreRes res = new MultiSearchGenreRes();
        res.setPeople(personResultList);
        res.setMovieResults(movieResultList);
        res.setTvShowResults(tvShowResultList);
        res.setTotalResults(multiSearchRes.getTotalResults());
        res.setTotalPages(multiSearchRes.getTotalPages());
        res.setPage(multiSearchRes.getPage());

//        res.setResults(results);
        return res;
    }

    public MovieDetail getMovieDetail(String movieId) {
        GenreSearchReq genreSearchReq = GenreSearchReq.builder()
                .apiKey(appConfig.getTmApi().getApiKey())
                .language("ko")
                .build();

        Map<String, Object> uriParams = new HashMap<>();
        uriParams.put("movieId", movieId);
        MovieDetail movieDetail = apiClient.get(appConfig.getTmApi().getHost() + SearchUri.MOVIE_DETAIL.getUri(), null, MovieDetail.class
                , MultiValueMapConverter.convert(mainObjectMapper, genreSearchReq), uriParams);

        // movie detail 필요한 것
        // 1. 연령정보
        ContentsCertificationSearchRes certificationSearchRes = this.getContentsCertification(movieId, "movie");
        movieDetail.setCertification(certificationSearchRes.getCertification());
        // 3. 제작진
        movieDetail.setCredit(this.getCredits("movie", movieId));

        return movieDetail;
    }

    public PersonDetail getPersonDetail(String personId) {
        GenreSearchReq genreSearchReq = GenreSearchReq.builder()
                .apiKey(appConfig.getTmApi().getApiKey())
                .language("ko")
                .build();

        Map<String, Object> uriParams = new HashMap<>();
        uriParams.put("personId", personId);
        PersonDetail personDetail = apiClient.get(appConfig.getTmApi().getHost() + SearchUri.PERSON_DETAIL.getUri(), null, PersonDetail.class
                , MultiValueMapConverter.convert(mainObjectMapper, genreSearchReq), uriParams);

        PersonCombinedCredit personCombinedCredit = getPersonCombinedCredit(personId);
        personDetail.setKnownForCastAndCrew(personCombinedCredit);

        return personDetail;
    }


    public ProviderSearchRes getProvider() {
        ProviderSearchReq providerSearchReq = ProviderSearchReq.builder()
                .apiKey(appConfig.getTmApi().getApiKey())
                .watchRegion("KR")
                .build();

        ProviderSearchRes providerSearchRes = apiClient.get(appConfig.getTmApi().getHost() + SearchUri.PROVIDER.getUri(), null, ProviderSearchRes.class
                , MultiValueMapConverter.convert(mainObjectMapper, providerSearchReq));

        // contents provider db insert
        List<Provider> providerVos = new ArrayList<>();
        providerSearchRes.getResults().forEach(res -> {
            Provider provider = Provider.builder()
                    .providerId(Integer.parseInt(res.getProviderId()))
                    .providerName(res.getProviderName())
                    .logoPath(res.getLogoPath())
                    .displayPriority(res.getDisplayPriority())
                    .build();
            providerVos.add(provider);
        });

        providerRepository.saveAll(providerVos);

        return providerSearchRes;
    }

    public ContentsProviderSearchRes getProviderByContentsId(String contentsId, String contentsType) {
        ProviderSearchReq providerSearchReq = ProviderSearchReq.builder()
                .apiKey(appConfig.getTmApi().getApiKey())
                .build();

        Map<String, Object> uriParams = new HashMap<>();
        uriParams.put("contentsId", contentsId);
        uriParams.put("contentsType", contentsType);

        ContentsProviderSearchRes contentsProviderSearchRes = apiClient.get(appConfig.getTmApi().getHost() + SearchUri.CONTENTS_PROVIDER_BY_ID.getUri(), null, ContentsProviderSearchRes.class
                , MultiValueMapConverter.convert(mainObjectMapper, providerSearchReq), uriParams);
        return contentsProviderSearchRes;
    }

    private Map<String, Genre> collectByGenreId(List<ResultInterface> resultList) {

        Map<String, Genre> genreVoMap = new HashMap<>();
        for (ResultInterface result : resultList) {
            for (Integer genreId : result.getGenreIds()) {
                if (genreVoMap.containsKey(genreId.toString())) {
                    Genre genre = genreVoMap.get(genreId.toString());
                    genre.getResults().add(result);
                } else {
                    Genre genre = genreRepository.findById(genreId).orElseThrow(() -> new ServiceException("404"));
                    List<ResultInterface> movieResults = new ArrayList<>();
                    movieResults.add(result);
                    genre.changeResults(movieResults);
                    genreVoMap.put(genreId.toString(), genre);
                }
            }
        }

        return genreVoMap;
    }

    public TvShowDetail getTvDetail(String tvId) {
        GenreSearchReq genreSearchReq = GenreSearchReq.builder()
                .apiKey(appConfig.getTmApi().getApiKey())
                .language("ko")
                .build();

        Map<String, Object> uriParams = new HashMap<>();
        uriParams.put("tvId", tvId);
        TvShowDetail tvShowDetail = apiClient.get(appConfig.getTmApi().getHost() + SearchUri.TV_DETAIL.getUri(), null, TvShowDetail.class
                , MultiValueMapConverter.convert(mainObjectMapper, genreSearchReq), uriParams);

        // movie detail 필요한 것
        // 1. 연령정보
        ContentsCertificationSearchRes certificationSearchRes = this.getContentsCertification(tvId, "tv");
        tvShowDetail.setCertification(certificationSearchRes.getCertification());
        // 2. 시청시간
        tvShowDetail.setTotalRuntime(getTvTotalRuntime(tvShowDetail));
        // 3. 제작진
        tvShowDetail.setCredit(this.getCredits("tv", tvId));

        return tvShowDetail;
    }

    public CreditSearchRes getCredits(String contentsType, String contentsId) {
        Map<String, Object> uriParams = new HashMap<>();
        uriParams.put("contentsId", contentsId);
        uriParams.put("contentsType", contentsType);

        CreditSearchReq creditSearchReq = CreditSearchReq
                .builder()
                .apiKey(appConfig.getTmApi().getApiKey())
                .language("ko-KR")
                .build();

        CreditSearchRes creditSearchRes = apiClient.get(appConfig.getTmApi().getHost() + SearchUri.CREDITS.getUri(), null, CreditSearchRes.class
                , MultiValueMapConverter.convert(mainObjectMapper, creditSearchReq), uriParams);

        List<GroupedCrew> crew = new ArrayList<>(creditSearchRes.getCrew().stream()
                .collect(Collectors.groupingBy(Crew::getDepartment,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                (List<Crew> mappedList) -> new GroupedCrew(mappedList.get(0).getDepartment(), mappedList)
                        )))
                .values());
        creditSearchRes.setGroupedCrew(crew);
        return creditSearchRes;

    }

    public ContentsCertificationSearchRes getContentsCertification(String contentsId, String contentsType) {
        Map<String, Object> uriParams = new HashMap<>();
        uriParams.put("contentsId", contentsId);

        ProviderSearchReq providerSearchReq = ProviderSearchReq.builder()
                .apiKey(appConfig.getTmApi().getApiKey())
                .build();

        ContentsCertificationSearchRes res = new ContentsCertificationSearchRes();
        res.setContentsId(contentsId);
        res.setContentsType(contentsType);
        if (contentsType.equals(ContentsType.MOVIE.getCode())) {

            MovieReleaseDates results = apiClient.get(appConfig.getTmApi().getHost() + SearchUri.MOVIE_CERTIFICATION.getUri(), null, MovieReleaseDates.class, MultiValueMapConverter.convert(mainObjectMapper, providerSearchReq), uriParams);

            ImmutablePair<MovieReleaseDates.Result.ReleaseDate, LocalDateTime> releaseDate = results.getResults().stream()
                    .filter(item -> item.getIso_3166_1().equals("KR"))
                    .map(item -> item.getReleaseDates())
                    .flatMap(List::stream)
                    .map(p -> new ImmutablePair<>(p, p.getReleaseDateMap()))
                    .max(Comparator.comparing(ImmutablePair::getRight))
                    .orElse(null);
            if (!ObjectUtils.isEmpty(releaseDate)) {
                res.setCertification(releaseDate.getLeft().getCertification());
            }
        } else if (contentsType.equals(ContentsType.TV.getCode())) {
            TvRating tvRating = apiClient.get(appConfig.getTmApi().getHost() + SearchUri.TV_CERTIFICATION.getUri(), null, TvRating.class, MultiValueMapConverter.convert(mainObjectMapper, providerSearchReq), uriParams);
            TvRating.Result result = tvRating.getResults().stream().filter(item -> item.getIso_3166_1().equals("KR")).findFirst().orElse(new TvRating.Result());

            if (!ObjectUtils.isEmpty(result.getRating())) {
                res.setCertification(result.getRating());
            }

        }
        return res;
    }

    public ContentsCommentsResponse getContentsComments(ContentsCommentsRequest contentsCommentsRequest) {
        Pageable page = PageRequest.of(contentsCommentsRequest.getPagination().getPageNumber(), contentsCommentsRequest.getPagination().getSize());
        Page<PhotoTicket> photoTickets = photoTicketRepository.findAllPhotoTicketByContentsTypeAndContentsId(contentsCommentsRequest.getContentsType(), contentsCommentsRequest.getContentsId(), page);
        ContentsCommentsResponse contentsCommentsResponse = new ContentsCommentsResponse();
        contentsCommentsResponse.setComments(new ArrayList<>());
        photoTickets.getContent().forEach(item -> {
            ContentsCommentsResponse.Comment comment = new ContentsCommentsResponse.Comment();
            comment.setComment(item.getComment());
            comment.setRating(item.getRating());
            contentsCommentsResponse.getComments().add(comment);
        });
        contentsCommentsResponse.setContentsType(contentsCommentsRequest.getContentsType());
        contentsCommentsResponse.setContentsId(contentsCommentsRequest.getContentsId());
        Pagination pagination = new Pagination();
        pagination.setSize(photoTickets.getSize());
        pagination.setTotalElements(photoTickets.getTotalElements());
        pagination.setPageNumber(photoTickets.getNumber());
        pagination.setTotalPages(photoTickets.getTotalPages());
        contentsCommentsResponse.setPagination(pagination);
        return contentsCommentsResponse;
    }

    public String getTvTotalRuntime(TvShowDetail tvShowDetail) {
        GenreSearchReq genreSearchReq = GenreSearchReq.builder()
                .apiKey(appConfig.getTmApi().getApiKey())
                .language("ko")
                .build();

        Map<String, Object> uriParams = new HashMap<>();
        int sum = 0;
        for (int i = 0; i < tvShowDetail.getNumberOfSeasons(); i++) {
            uriParams.put("tvId", tvShowDetail.getId());
            uriParams.put("seasonNumber", i + 1);
            TvSeasonDetail seasonDetail = apiClient.get(appConfig.getTmApi().getHost() + SearchUri.TV_SEASON_DETAIL.getUri(), null, TvSeasonDetail.class
                    , MultiValueMapConverter.convert(mainObjectMapper, genreSearchReq), uriParams);
            sum += seasonDetail.getEpisodes().stream().mapToInt(Episode::getRuntime).sum();
        }
        return String.valueOf(sum);
    }

    public PersonCombinedCredit getPersonCombinedCredit(String personId) {
        GenreSearchReq genreSearchReq = GenreSearchReq.builder()
                .apiKey(appConfig.getTmApi().getApiKey())
                .language("ko")
                .build();
        Map<String, Object> uriParams = new HashMap<>();
        uriParams.put("personId", personId);
        PersonCombinedCredit personCombinedCredit = apiClient.get(appConfig.getTmApi().getHost() + SearchUri.PERSON_DETAIL_CREDIT.getUri(), null, PersonCombinedCredit.class
                , MultiValueMapConverter.convert(mainObjectMapper, genreSearchReq), uriParams);

        return personCombinedCredit;
    }

    public ContentsSummary getMovieOrTvSummary(String contentsId, String contentsType) {
        ContentsSummary contentsSummary = new ContentsSummary();
        if (!StringUtils.hasText(contentsId) || !StringUtils.hasText(contentsType)) return contentsSummary;
        Genre genre = null;
        contentsSummary.setContentsId(contentsId);
        contentsSummary.setContentsType(contentsType);
        if (contentsType.equals(ContentsType.MOVIE.getCode())) {
            MovieDetail movieDetail = new MovieDetail();
            try {
                movieDetail = this.getMovieDetail(contentsId);
                contentsSummary.setTitle(movieDetail.getTitle());
                if (movieDetail.getGenreIds() != null) {
                    genre = genreRepository.findByGenreIdAndGenreType(String.valueOf(movieDetail.getGenreIds().get(0)), contentsType).orElse(Genre.builder().build());
                }
            } catch (ServiceException e) {
                // do nothing
            }
        } else if (contentsType.equals(ContentsType.TV.getCode())) {
            TvShowDetail tvShowDetail = new TvShowDetail();
            try {
                tvShowDetail = this.getTvDetail(contentsId);
                contentsSummary.setTitle(tvShowDetail.getName());
                if (tvShowDetail.getGenreIds() != null) {
                    genre = genreRepository.findByGenreIdAndGenreType(String.valueOf(tvShowDetail.getGenreIds().get(0)), contentsType).orElse(Genre.builder().build());
                }
            } catch (ServiceException e) {
                // do nothing
            }
        }
        contentsSummary.setGenre(genre);
        return contentsSummary;

    }

    public MultiSearchRes getTrendingList() {
        GenreSearchReq genreSearchReq = GenreSearchReq.builder()
                .apiKey(appConfig.getTmApi().getApiKey())
                .language("ko-kr")
                .build();

        MultiSearchRes res = apiClient.get(appConfig.getTmApi().getHost() + SearchUri.TRENDING_ALL.getUri(), null, MultiSearchRes.class,
                MultiValueMapConverter.convert(mainObjectMapper, genreSearchReq));
        res.setResults(res.getResults().subList(0, 6));
        return res;
    }

    public TvSeasonDetail getTvSeasonDetail(String tvId, String seasonNumber) {
        return new TvSeasonDetail();
    }

    public CreditSearchRes getTvSeasonCredits(String tvId, String seasonNumber) {
        Map<String, Object> uriParams = new HashMap<>();
        uriParams.put("tvId", tvId);
        uriParams.put("seasonNumber", seasonNumber);

        CreditSearchReq creditSearchReq = CreditSearchReq
                .builder()
                .apiKey(appConfig.getTmApi().getApiKey())
                .language("ko-KR")
                .build();

        CreditSearchRes creditSearchRes = apiClient.get(appConfig.getTmApi().getHost() + SearchUri.TV_SEASON_CREDIT.getUri(), null, CreditSearchRes.class
                , MultiValueMapConverter.convert(mainObjectMapper, creditSearchReq), uriParams);

        List<GroupedCrew> crew = new ArrayList<>(creditSearchRes.getCrew().stream()
                .collect(Collectors.groupingBy(Crew::getDepartment,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                (List<Crew> mappedList) -> new GroupedCrew(mappedList.get(0).getDepartment(), mappedList)
                        )))
                .values());
        creditSearchRes.setGroupedCrew(crew);
        return creditSearchRes;
    }

    public MediaSearchResponse getMediaSearchResults(String query, int page, String region) {
        MovieSearchRes movieSearchRes = this.getMovieListByQuery(query, page);
        TvSearchRes tvSearchRes = this.getTvByQuery(query, page, "");

        List<MediaSearchResponse.Media> mediaList = new ArrayList<>();
        for (MovieResult movieResult : movieSearchRes.getResults()) {
            MediaSearchResponse.Media media = getMedia(movieResult);
            mediaList.add(media);
        }
        for (TvShowResult tvShowResult : tvSearchRes.getResults()) {
            MediaSearchResponse.Media media = getMedia(tvShowResult);
            mediaList.add(media);
        }

        MediaSearchResponse mediaSearchResponse = new MediaSearchResponse();
        mediaSearchResponse.setMedias(mediaList);
        return  mediaSearchResponse;
    }

    private MediaSearchResponse.Media getMedia(MovieResult movieResult) {
        MediaSearchResponse.Media media = new MediaSearchResponse.Media();
        media.setPosterPath(movieResult.getPosterPath());
        media.setAdult(movieResult.isAdult());
        media.setOverview(movieResult.getOverview());
        media.setReleaseDate(movieResult.getReleaseDate());
        media.setGenreIds(movieResult.getGenreIds());
        media.setContentsId(movieResult.getId());
        media.setContentsType("movie");
        media.setTitle(movieResult.getTitle());
        media.setOriginalLanguage(movieResult.getOriginalLanguage());
        media.setOriginalTitle(movieResult.getOriginalTitle());
        media.setBackdropPath(movieResult.getBackdropPath());
        return media;
    }

    private MediaSearchResponse.Media getMedia(TvShowResult tvShowResult) {
        MediaSearchResponse.Media media = new MediaSearchResponse.Media();
        media.setPosterPath(tvShowResult.getPosterPath());
//            media.setAdult();
        media.setOverview(tvShowResult.getOverview());
        media.setReleaseDate(tvShowResult.getFirstAirDate());
        media.setGenreIds(tvShowResult.getGenreIds());
        media.setContentsId(tvShowResult.getId());
        media.setContentsType("tv");
        media.setTitle(tvShowResult.getName());
        media.setOriginalLanguage(tvShowResult.getOriginalLanguage());
        media.setOriginalTitle(tvShowResult.getOriginalName());
        media.setBackdropPath(tvShowResult.getBackdropPath());
        return media;
    }

    public MultiRuntimeRes getRuntimes(MultiRuntimeReq multiRuntimeReq) throws Exception {
        MultiRuntimeRes multiRuntimeRes = new MultiRuntimeRes();
        List<MultiRuntimeRes.RuntimeRes> runtimes = new ArrayList<>();
        for (MultiRuntimeReq.RuntimeReq req : multiRuntimeReq.getRuntimes()) {
            MultiRuntimeRes.RuntimeRes res = new MultiRuntimeRes.RuntimeRes();
            if ("movie".equals(req.getContentsType())) {
                MovieDetail movieDetail = this.getMovieDetail(req.getContentsId());
                res.setRuntime(movieDetail.getRuntime());
                res.setContentsType("movie");
                res.setContentsId(req.getContentsId());
            } else {
                TvShowDetail tvShowDetail = getTvDetail(req.getContentsId());
                res.setRuntime(Long.parseLong(tvShowDetail.getTotalRuntime()));
                res.setContentsType("tv");
                res.setContentsId(req.getContentsId());
            }
            runtimes.add(res);
        }
        multiRuntimeRes.setRuntimes(runtimes);
        return multiRuntimeRes;
    }
}
