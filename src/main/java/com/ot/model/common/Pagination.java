package com.ot.model.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pagination {

    private int size;

    private int pageNumber;

    private long totalElements;

    private int totalPages;

}
