package com.ot.model.auth;

import com.ot.model.CommonResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse extends CommonResponse {

    private TokenInfo tokenInfo;

}
