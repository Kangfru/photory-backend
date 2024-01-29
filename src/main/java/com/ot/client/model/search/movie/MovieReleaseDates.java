package com.ot.client.model.search.movie;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
public class MovieReleaseDates {

    private String id;

    private List<Result> results;


    @Getter
    @Setter
    public static class Result {
        private String iso_3166_1;

        private List<ReleaseDate> releaseDates;

        @Getter
        @Setter
        public static class ReleaseDate {

            private String certification;

            private String iso_639_1;

            private String releaseDate;

            private int type;

            private String note;

            public LocalDateTime getReleaseDateMap() {
                return LocalDateTime.parse(releaseDate, DateTimeFormatter.ISO_DATE_TIME);
            }

        }
    }

}
