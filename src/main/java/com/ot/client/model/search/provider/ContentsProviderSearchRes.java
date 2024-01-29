package com.ot.client.model.search.provider;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ContentsProviderSearchRes {

    private String link;

    List<ProviderDetail> buy;

    List<ProviderDetail> rent;

    // 구독
    List<ProviderDetail> flatrate;

}
