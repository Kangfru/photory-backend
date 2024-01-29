package com.ot.client.model.search.genre;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ot.client.model.CommonRes;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenreSearchRes extends CommonRes {

    private List<GenreResult> genres;

}
