package com.ot.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthorizationCode {

    WAIT("0", "인증 대기 중"),
    SUCCESS("1", "인증 성공"),
    EXPIRATION("9", "인증 시간 만료");

    private final String code;

    private final String description;

}
