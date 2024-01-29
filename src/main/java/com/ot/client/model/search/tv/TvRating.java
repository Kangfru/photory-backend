package com.ot.client.model.search.tv;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class TvRating {

    private int id;

    private List<Result> results;

    @Getter
    @Setter
    public static class Result {

        private String iso_3166_1;

        private String rating;

    }

}
