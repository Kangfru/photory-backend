package com.ot.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ServiceException extends RuntimeException {

    private String errorCode;

    private String description;

    private HttpStatus httpStatus;

    public ServiceException(String errorCode) {
        this.errorCode = errorCode;
    }

    public ServiceException(String errorCode, String description) {
        this.errorCode = errorCode;
        this.description = description;
    }

    public ServiceException (String errorCode, String description, HttpStatus httpStatus) {
        this.errorCode = errorCode;
        this.description = description;
        this.httpStatus = httpStatus;
    }

}
