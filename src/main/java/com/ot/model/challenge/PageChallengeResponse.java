package com.ot.model.challenge;

import com.ot.model.common.Pagination;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PageChallengeResponse {

    private List<ChallengeResponse> challenges;

    private Pagination pagination;

}
