package com.ot.client.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonRes {

    private int totalResults;

    private int totalPages;

    private int page;

    private boolean success;

    private int statusCode;

    private String statusMessage;

}
