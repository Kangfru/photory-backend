package com.ot.client.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContentsType {

    TV("tv", "tv"),
    MOVIE("movie", "movie");

    private final String code;

    private final String description;


}
