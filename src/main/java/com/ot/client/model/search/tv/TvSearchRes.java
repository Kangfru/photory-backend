package com.ot.client.model.search.tv;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ot.client.model.CommonRes;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class TvSearchRes extends CommonRes {

    private List<TvShowResult> results;

}
