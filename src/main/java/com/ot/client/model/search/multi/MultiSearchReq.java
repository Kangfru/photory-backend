package com.ot.client.model.search.multi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ot.client.model.BaseSearchReqModel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@ToString
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MultiSearchReq extends BaseSearchReqModel {

    private String region;

}
