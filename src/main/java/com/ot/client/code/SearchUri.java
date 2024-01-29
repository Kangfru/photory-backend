package com.ot.client.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpMethod;
import org.w3c.dom.DOMLocator;

@Getter
@AllArgsConstructor
public enum SearchUri {

    MULTI_SEARCH("/search/multi", HttpMethod.GET),

    TV_SHOW_SEARCH("/search/tv", HttpMethod.GET),

    PERSON_SEARCH("/search/person", HttpMethod.GET),

    MOVIE_SEARCH("/search/movie", HttpMethod.GET),

    GENRE_SEARCH("/genre/{type}/list", HttpMethod.GET),

    MOVIE_DETAIL("/movie/{movieId}", HttpMethod.GET),

    TV_DETAIL("/tv/{tvId}", HttpMethod.GET),

    PERSON_DETAIL("/person/{personId}", HttpMethod.GET),

    PERSON_DETAIL_CREDIT("/person/{personId}/combined_credits", HttpMethod.GET),

    PROVIDER("/watch/providers/tv", HttpMethod.GET),

    CONTENTS_PROVIDER_BY_ID("/{contentsType}/{contentsId}/watch/providers", HttpMethod.GET),

    CREDITS("/{contentsType}/{contentsId}/credits", HttpMethod.GET),

    MOVIE_CERTIFICATION("/movie/{contentsId}/release_dates", HttpMethod.GET),

    TV_CERTIFICATION("/tv/{contentsId}/content_ratings", HttpMethod.GET),

    TV_SEASON_DETAIL("/tv/{tvId}/season/{seasonNumber}", HttpMethod.GET),

    TV_SEASON_CREDIT("/tv/{tvId}}/season/{seasonNumber}/credits", HttpMethod.GET),

//    TRENDING_ALL("trending/all/{timeWindow}", HttpMethod.GET)
    TRENDING_ALL("/trending/all/day", HttpMethod.GET)
    ;

    private final String uri;

    private final HttpMethod httpMethod;

}
