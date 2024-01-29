package com.ot.client.model.search.tv;

import com.ot.client.model.BaseSearchReqModel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@ToString
public class TvSearchReq extends BaseSearchReqModel {

    private String firstAirDateYear;

}
