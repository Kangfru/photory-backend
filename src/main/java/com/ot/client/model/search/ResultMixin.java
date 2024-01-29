package com.ot.client.model.search;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.ot.client.model.search.movie.MovieResult;
import com.ot.client.model.search.person.PersonResult;
import com.ot.client.model.search.tv.TvShowResult;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "media_type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MovieResult.class, name = "movie"),
        @JsonSubTypes.Type(value = TvShowResult.class, name = "tv"),
        @JsonSubTypes.Type(value = PersonResult.class, name = "person")
})
public class ResultMixin {
}
