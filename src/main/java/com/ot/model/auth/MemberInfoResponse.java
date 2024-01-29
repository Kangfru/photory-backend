package com.ot.model.auth;

import com.ot.model.CommonResponse;
import com.ot.model.member.ReturnableMember;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberInfoResponse extends CommonResponse {

    private ReturnableMember member;

}
