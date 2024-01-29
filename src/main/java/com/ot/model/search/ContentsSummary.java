package com.ot.model.search;

import com.ot.repository.contents.entity.Genre;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContentsSummary {

    private String contentsType;

    private String contentsId;

    private String title;

    private Genre genre;

}
