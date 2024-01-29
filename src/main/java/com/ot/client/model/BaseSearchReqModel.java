package com.ot.client.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class BaseSearchReqModel {

    private String apiKey;

    private String language;

    private int page;

    private String query;

    private boolean includeAdult;

}
