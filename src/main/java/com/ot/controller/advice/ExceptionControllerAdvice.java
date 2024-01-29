package com.ot.controller.advice;

import com.ot.exception.ServiceException;
import com.ot.model.CommonResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler({ServiceException.class})
    public CommonResponse handleServiceException(ServiceException e) {
        log.error(e.getErrorCode());
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setResponseCode(e.getErrorCode());
        if (StringUtils.isNotEmpty(e.getDescription())) {
            commonResponse.setResponseMessage(e.getDescription());
        }
        if (ObjectUtils.isNotEmpty(e.getHttpStatus())) {
            commonResponse.setHttpStatus(e.getHttpStatus());
        }
        return commonResponse;
    }

    @ExceptionHandler({Exception.class})
    public CommonResponse handleUnknownException(Exception e) {
        log.error("", e);
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setResponseCode("500");
        commonResponse.setResponseMessage("서버 오류가 발생하였습니다.");
        commonResponse.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return commonResponse;
    }

    @ExceptionHandler({AuthenticationException.class})
    public CommonResponse handleAuthException(AuthenticationException e) {
//        log.error("", e);
        CommonResponse commonResponse = new CommonResponse();
        if (e instanceof UsernameNotFoundException) {
            commonResponse.setResponseCode("404");
            commonResponse.setHttpStatus(HttpStatus.NOT_FOUND);
            commonResponse.setResponseMessage("찾을 수 없는 이메일입니다.");
        } else if (e instanceof BadCredentialsException){
            commonResponse.setResponseCode("403");
            commonResponse.setHttpStatus(HttpStatus.FORBIDDEN);
            commonResponse.setResponseMessage("아이디 또는 비밀번호가 맞지 않습니다. 다시 확인해주세요.");
        } else {
            commonResponse.setResponseCode("500");
            commonResponse.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            commonResponse.setResponseMessage("알 수 없는 에러.");
        }
        return commonResponse;
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public CommonResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setHttpStatus(HttpStatus.NOT_FOUND);
        commonResponse.setResponseCode("404");
        BindingResult bindingResult = e.getBindingResult();

        StringBuilder stringBuilder = new StringBuilder();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            stringBuilder.append(fieldError.getField()).append(":");
            stringBuilder.append(fieldError.getDefaultMessage());
            stringBuilder.append(", ");
        }
        commonResponse.setResponseMessage("파라미터가 잘못되었습니다 -> " + stringBuilder);

        return commonResponse;
    }

    @ExceptionHandler({JwtException.class})
    public CommonResponse handleClaimJwtException(JwtException e) {
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setHttpStatus(HttpStatus.FORBIDDEN);
        if (e instanceof ExpiredJwtException) {
            commonResponse.setResponseCode("4031");
            commonResponse.setResponseMessage("만료된 토큰입니다.");
            commonResponse.setDetailMessage("만료된 토큰입니다.");
        } else if (e instanceof io.jsonwebtoken.security.SecurityException || e instanceof MalformedJwtException) {
            commonResponse.setResponseCode("4032");
            commonResponse.setResponseMessage("유효하지 않은 토큰입니다.");
        }
        return commonResponse;
    }

}
