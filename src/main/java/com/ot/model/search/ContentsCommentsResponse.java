package com.ot.model.search;

import com.ot.model.common.Pagination;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ContentsCommentsResponse {

    private String contentsId;

    private String contentsType;

    private List<Comment> comments;

    private Pagination pagination;

    @Getter
    @Setter
    public static class Comment {
        private String comment;

        private int rating;
    }

}
