package com.ot.controller;

import com.ot.client.model.CommonRes;
import com.ot.config.AppConfig;
import com.ot.exception.ServiceException;
import com.ot.model.CommonResponse;
import com.ot.model.auth.*;
import com.ot.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AppConfig appConfig;

    private final MemberService memberService;

    @GetMapping("/authorization")
    @Operation(summary = "이메일 확인 키 전송", description = "이메일 확인 키 전송 - 키 전송 이후 이메일 통해 /authorization/{authorization} 으로 검증한다.")
    public CommonResponse sendAuthKey(@RequestHeader Map<String, String> header,
                                      @RequestParam String email) throws Exception {
        if (email.isEmpty()) {
            throw new ServiceException("AUTH01", "Missing Required Parameter", HttpStatus.BAD_REQUEST);
        }
        if (memberService.isDuplicateEmail(email)) {
            throw new ServiceException("AUTH02", "Duplicated Email, Already registered", HttpStatus.BAD_REQUEST);
        }
        memberService.sendAuthEmail(email);
        return new CommonResponse();
    }

    @GetMapping("/authorization/{authKey}/{type}")
    @Operation(summary = "이메일 확인 키 검증", description = "이메일 확인 키 검증 (이메일 통해 인입)")
    public String verifyAuthKey(@RequestHeader Map<String, String> header,
                                        @PathVariable String authKey,
                                        @PathVariable String type) throws Exception {
        if (StringUtils.isEmpty(authKey)) {
            throw new ServiceException("AUTH01", "Missing Required Parameter", HttpStatus.BAD_REQUEST);
        }
        return memberService.verifyAuthKey(authKey, type);
    }

    @PostMapping("/user")
    @Operation(summary = "회원 가입", description = "회원 가입.")
    public SignUpResponse signUpUser(@RequestHeader Map<String, String> header,
                                     @RequestBody @Validated SignUpRequest signUpRequest) {
        return memberService.signUpUser(signUpRequest);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "로그인 시도 성공 시 jwt token return 해당 토큰을 통해 auth 필오한 api header에 Bearer 명시")
    public LoginResponse login(@RequestHeader Map<String, String> header,
                               @RequestBody @Validated LoginRequest loginRequest) {
        return memberService.login(loginRequest) ;
    }

    @PostMapping("/refresh")
    @Operation(summary = "refresh token 으로 access token 새로 발급받기", description = "refresh token 으로 access token 새로 발급받기 body 필수.", responses = {
            @ApiResponse(responseCode = "4031", description = "만료된 토큰 일 때"),
            @ApiResponse(responseCode = "4032", description = "유효하지 않은 토큰 일 때")
    })
    public CommonResponse refreshToken(@RequestHeader Map<String, String> header,
                                  @RequestBody @Validated RefreshTokenRequest refreshTokenRequest) throws Exception {

        return memberService.refreshToken(refreshTokenRequest);
    }

    @Operation(summary = "이메일 인증 성공 여부 확인하기", description = "이메일 인증성공 여부 확인 하기")
    @GetMapping("/authorization/{email}/validation")
    public CommonResponse isAuthorizationSuccess(@PathVariable String email) throws Exception {
        return memberService.isAuthorizationSuccess(email);
    }

    @Operation(summary = "닉네임 중복 여부 확인하기", description = "닉네임 중복 여부 확인 하기")
    @GetMapping("/user/nickname/{nickName}")
    public CommonResponse isExistsNickName(@PathVariable String nickName) throws Exception {
        boolean isDuplicate = memberService.isDuplicateNickName(nickName);
        if (isDuplicate) {
            throw new ServiceException("AUTH02", "Duplicated nickname", HttpStatus.BAD_REQUEST);
        }
        return new CommonResponse();
    }

    @GetMapping("/authorization/password")
    @Operation(summary = " 확인 키 전송", description = "이메일 확인 키 전송 - 키 전송 이후 이메일 통해 인증 (Password 찾기 용)")
    public CommonResponse sendPasswordKeyForFind(@RequestHeader Map<String, String> header,
                                                 @RequestParam String email) throws Exception {
        if (email.isEmpty()) {
            throw new ServiceException("404", "Missing Required Parameter", HttpStatus.BAD_REQUEST);
        }
        memberService.sendPasswordKeyForFind(email);
        return new CommonResponse();
    }

    @GetMapping("/user/detail")
    @Operation(summary = "내 정보 조회")
    public MemberInfoResponse getMyMemberInfo(@RequestHeader Map<String, String> header) throws Exception {
        return memberService.getMyMemberInfo();
    }

    @PutMapping("/user")
    @Operation(summary = "내 정보 수정")
    public CommonResponse updateMemberInfo(@RequestBody @Validated MemberUpdateRequest memberUpdateRequest) {
        return memberService.updateMemberInfo(memberUpdateRequest);
    }

    @PutMapping("/user/password")
    @Operation(summary = "비밀번호 찾기 이후 비밀번호 수정")
    public CommonResponse updatePassword(@RequestBody @Validated PasswordUpdateRequest passwordUpdateRequest) {
        return memberService.updatePassword(passwordUpdateRequest);
    }

    @DeleteMapping("/user")
    @Operation(summary = "회원 탈퇴")
    public CommonResponse resignMember() {
        return memberService.resignMember();
    }

    @GetMapping("/user/detail/dashboard")
    @Operation(summary = "내 정보 조회 - 도장깨기판 개수, 포토티켓 개수, 본 작품 시간 등")
    public MyDashboardResponse getUserDetailDashboard() {
        return memberService.getUserDetailDashboard();
    }
}
