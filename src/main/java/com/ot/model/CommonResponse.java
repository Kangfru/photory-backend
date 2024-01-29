package com.ot.model;

import com.ot.client.model.CommonRes;
import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class CommonResponse {

    public CommonResponse() {
        this.responseCode = "200";
        this.responseMessage = "Success";
        this.httpStatus = HttpStatus.OK;
    }

    private String responseCode;

    private String responseMessage;

    private String detailMessage;

    private String moreInfo;

    private HttpStatus httpStatus;

}
