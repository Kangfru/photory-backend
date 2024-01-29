package com.ot.client.model.search.movie;

import com.ot.client.model.BaseSearchReqModel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@SuperBuilder
public class MovieSearchReq extends BaseSearchReqModel {

    private String region;

    private int year;

    private int primaryReleaseYear;

}
