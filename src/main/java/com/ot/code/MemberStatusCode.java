package com.ot.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberStatusCode {

    NORMAL("1", "정상 일반 회원"),
    SLEEP("8", "휴면"),
    RESIGN("9", "탈퇴 상태");

    private String code;

    private String description;

}
