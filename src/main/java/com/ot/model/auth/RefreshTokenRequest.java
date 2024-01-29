package com.ot.model.auth;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class RefreshTokenRequest {

    @NotEmpty
    private String refreshToken;

}
