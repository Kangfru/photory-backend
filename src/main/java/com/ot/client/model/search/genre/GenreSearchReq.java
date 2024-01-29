package com.ot.client.model.search.genre;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GenreSearchReq {

    private String apiKey;

    private String language;

}
