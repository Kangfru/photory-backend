package com.ot.client.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberAuthorizationStatus {

    SUCCESS("1", "인증 성공"),
    NOT_YET("0", "미인증");

    private final String code;

    private final String description;

}
