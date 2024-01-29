package com.ot.client.model.search.movie;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ot.client.model.CommonRes;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieSearchRes extends CommonRes {

    private List<MovieResult> results;

}
