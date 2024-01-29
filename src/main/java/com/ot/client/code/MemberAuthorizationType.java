package com.ot.client.code;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberAuthorizationType {

    JOIN("1", "to use in join"),
    FIND("2", "to find password");

    private final String code;

    private final String description;

}
