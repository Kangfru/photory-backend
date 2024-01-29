package com.ot.client.model.search.provider;

import com.ot.client.model.BaseSearchReqModel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@SuperBuilder
public class ProviderSearchReq {

    private String watchRegion;

    private String apiKey;

}
