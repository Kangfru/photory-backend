package com.ot.client.model.search.credits;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreditSearchReq {

    private String apiKey;

    private String language;

}
