package com.ot.model.search;

import com.ot.model.CommonResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MediaSearchResponse extends CommonResponse {

    private List<Media> medias;

    @Getter
    @Setter
    public static class Media {

        private String posterPath;

        private boolean adult;

        private String overview;

        private String releaseDate;

        private List<Integer> genreIds;

        private int contentsId;

        private String originalTitle;

        private String originalLanguage;

        private String backdropPath;

        private String title;

        private String contentsType;
    }

}
