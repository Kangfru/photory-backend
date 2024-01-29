package com.ot.client.model.search.person;

import com.ot.client.model.BaseSearchReqModel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@SuperBuilder
public class PersonSearchReq extends BaseSearchReqModel {

    private String region;

}
