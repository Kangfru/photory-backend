package com.ot.model.search;

import com.ot.model.common.Pagination;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ContentsCommentsRequest {

    private String contentsId;

    private String contentsType;

    private Pagination pagination;

}
