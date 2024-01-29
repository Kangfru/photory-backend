package com.ot.service;

import com.ot.client.code.ChallengeCode;
import com.ot.client.code.MemberAuthorizationStatus;
import com.ot.client.code.MemberAuthorizationType;
import com.ot.code.AuthorizationCode;
import com.ot.code.MemberStatusCode;
import com.ot.component.JwtTokenProvider;
import com.ot.config.AppConfig;
import com.ot.exception.ServiceException;
import com.ot.model.CommonResponse;
import com.ot.model.auth.*;
import com.ot.model.challenge.ChallengeDashboardResponse;
import com.ot.model.member.ReturnableMember;
import com.ot.repository.challenge.repository.ChallengeContentRepository;
import com.ot.repository.challenge.repository.ChallengeRecordRepository;
import com.ot.repository.member.MemberAuthorizationRepository;
import com.ot.repository.member.MemberRepository;
import com.ot.repository.member.entity.Member;
import com.ot.repository.member.entity.MemberAuthorization;
import com.ot.repository.photo_tikcet.PhotoTicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final AppConfig appConfig;

    private final JavaMailSenderImpl emailSender;

    private final PasswordEncoder bCryptPasswordEncoder;

    private final MemberRepository memberRepository;

    private final MemberAuthorizationRepository memberAuthorizationRepository;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final JwtTokenProvider jwtTokenProvider;

    private final AmazonEmailSendService amazonEmailSendService;

    private final ChallengeRecordRepository challengeRecordRepository;

    private final ChallengeContentRepository challengeContentRepository;

    private final PhotoTicketRepository photoTicketRepository;

    public boolean isDuplicateEmail(String email) {
        return memberRepository.findByEmailAndStatus(email, MemberStatusCode.NORMAL.getCode()).isPresent();
    }

    public void sendAuthEmail(String email) throws Exception {
        String generatedKey = generateAuthKey();
        MemberAuthorization memberAuthorization = new MemberAuthorization();
        memberAuthorization.setAuthorizationKey(generatedKey);
        memberAuthorization.setEmail(email);
        memberAuthorization.setStatus(AuthorizationCode.WAIT.getCode());

        LocalDateTime now = LocalDateTime.now();

        memberAuthorization.setRegDate(now);
        memberAuthorization.setExpireDate(now.plusMinutes(5));
        memberAuthorization.setType(MemberAuthorizationType.JOIN.getCode());
        try {
            memberAuthorizationRepository.save(memberAuthorization);
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error("auth key save failed");
            throw new ServiceException("500", "InternalServer Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        MimeMessage mimeMessage = createMimeMessage(email, generatedKey);
//        emailSender.send(mimeMessage);
        amazonEmailSendService.send(mimeMessage.getSubject(), mimeMessage.getContent().toString(), email);
    }

    public String verifyAuthKey(String authKey, String type) throws Exception {
        MemberAuthorization memberAuthorization = memberAuthorizationRepository.findByAuthorizationKeyAndType(authKey, type)
                .orElseThrow(() -> new ServiceException("404", "Can't find Authorization Information", HttpStatus.NOT_FOUND));
        if (AuthorizationCode.SUCCESS.getCode().equals(memberAuthorization.getStatus())) {
            throw new ServiceException("208", "Already Authorized", HttpStatus.ALREADY_REPORTED);
        }
        if (memberAuthorization.getExpireDate().isBefore(LocalDateTime.now())) {
            throw new ServiceException(("500"), "Authorization key expired", HttpStatus.BAD_REQUEST);
        }

        memberAuthorization.setStatus(AuthorizationCode.SUCCESS.getCode());
        memberAuthorizationRepository.save(memberAuthorization);

        ClassPathResource htmlSource = new ClassPathResource("/mail/confirm.html");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        htmlSource.getInputStream(),
                        StandardCharsets.UTF_8
                )
        );
        return reader.lines().collect(Collectors.joining(System.lineSeparator()));
    }

    public SignUpResponse signUpUser(SignUpRequest signUpRequest) {
        if (isDuplicateEmail(signUpRequest.getEmail())) {
            throw new ServiceException("500", "Already Registered", HttpStatus.BAD_REQUEST);
        }
        // sign 저장
        SignUpRequest sign = signUpRequest.hashPassword(bCryptPasswordEncoder);
        Member member = new Member();
        member.setEmail(sign.getEmail());
        member.setPassword(sign.getPassword());
        member.setNickName(signUpRequest.getNickName());
        member.setRoles(List.of("MEMBER"));
        member.setStatus(MemberStatusCode.NORMAL.getCode());
        memberRepository.save(member);
        SignUpResponse signUpResponse = new SignUpResponse();
        return signUpResponse;
    }



    private MimeMessage createMimeMessage(String to, String generatedKey) throws MessagingException, IOException {

        MimeMessage message = emailSender.createMimeMessage();

        message.addRecipients(MimeMessage.RecipientType.TO, to);// 보내는 대상
        message.setSubject("Photory 회원가입 이메일 인증");// 제목

        ClassPathResource htmlSource = new ClassPathResource("/mail/sign-up.html");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        htmlSource.getInputStream(),
                        StandardCharsets.UTF_8
                )
        );
        String msgg = reader.lines().collect(Collectors.joining(System.lineSeparator()));
//        String confirmLink = "https://127.0.0.1" + "/api/v1/auth/authorization/" + generatedKey + "/1"; // 메일에 인증번호 넣기
        String confirmLink = appConfig.getHost() + "/api/v1/auth/authorization/" + generatedKey + "/1"; // 메일에 인증번호 넣기
        msgg = msgg.replace("href=\"#\"", "href=" + confirmLink);
        message.setText(msgg, "utf-8", "html");// 내용, charset 타입, subtype
        // 보내는 사람의 이메일 주소, 보내는 사람 이름
        message.setFrom(new InternetAddress("no-reply@photory-app.com", "photory_admin"));// 보내는 사람

        return message;
    }

    private String generateAuthKey() {
        Random ran = new Random();
        StringBuilder sb = new StringBuilder();
        int num  = 0;
        do {
            num = ran.nextInt(75) + 48;
            if (num <= 57 || (num >= 65 && num <= 90) || num >= 97) {
                sb.append((char) num);
            }
        } while (sb.length() < 20);

        return sb.toString().toLowerCase();
    }

    public LoginResponse login(LoginRequest loginRequest) {
        LoginResponse loginResponse = new LoginResponse();

        // 1. Login ID/PW 를 기반으로 Authentication 객체 생성
        // 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        // 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
        // authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
        Authentication authentication = authenticationManagerBuilder
                .getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication, loginRequest.isRememberMe());
        loginResponse.setTokenInfo(tokenInfo);

        return loginResponse;
    }


    public CommonResponse isAuthorizationSuccess(String email) {
        CommonResponse commonResponse = new CommonResponse();
        List<MemberAuthorization> memberAuthorizationVos = memberAuthorizationRepository.findAllByEmailAndType(email, MemberAuthorizationType.JOIN.getCode());
        if (memberAuthorizationVos.isEmpty()) {
            throw new ServiceException("404", "찾을 수 없는 이메일입니다.", HttpStatus.NOT_FOUND);
        }

        memberAuthorizationVos.stream()
                .filter(vo -> vo.getStatus().equals(MemberAuthorizationStatus.SUCCESS.getCode()))
                .findAny()
                .orElseThrow(() -> new ServiceException("401", "인증되지 않았습니다.", HttpStatus.UNAUTHORIZED));

        commonResponse.setResponseCode("200");
        commonResponse.setDetailMessage("이메일 인증에 성공하였습니다.");
        commonResponse.setHttpStatus(HttpStatus.OK);
        return commonResponse;
    }

    public boolean isDuplicateNickName(String nickName) {
        return memberRepository.findByNickName(nickName).isPresent();
    }

    public CommonResponse refreshToken(RefreshTokenRequest refreshTokenRequest) throws Exception {
        String accessToken = jwtTokenProvider.validateRefreshToken(refreshTokenRequest.getRefreshToken());
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setResponseCode("200");
        commonResponse.setHttpStatus(HttpStatus.OK);
        commonResponse.setResponseMessage("정상처리");
        commonResponse.setMoreInfo(accessToken);
        return commonResponse;
    }

    public void sendPasswordKeyForFind(String email) throws IOException, MessagingException {
        String generatedKey = generateAuthKey();
        MemberAuthorization memberAuthorization = new MemberAuthorization();
        memberAuthorization.setAuthorizationKey(generatedKey);
        memberAuthorization.setEmail(email);
        memberAuthorization.setStatus(AuthorizationCode.WAIT.getCode());

        LocalDateTime now = LocalDateTime.now();
        memberAuthorization.setRegDate(now);
        memberAuthorization.setExpireDate(now.plusMinutes(5));
        memberAuthorization.setType(MemberAuthorizationType.FIND.getCode());
        try {
            memberAuthorizationRepository.save(memberAuthorization);
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error("auth key save failed");
            throw new ServiceException("500", "InternalServer Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        MimeMessage mimeMessage = createPasswordKeyMimeMessage(email, generatedKey);
//        emailSender.send(mimeMessage);
        amazonEmailSendService.send(mimeMessage.getSubject(), mimeMessage.getContent().toString(), email);
    }

    public CommonResponse updatePassword(PasswordUpdateRequest request) {
        List<MemberAuthorization> memberAuthorizationVos = memberAuthorizationRepository.findAllByEmailAndType(request.getEmail(), MemberAuthorizationType.FIND.getCode());
        memberAuthorizationVos.stream()
                .filter(vo -> vo.getStatus().equals(MemberAuthorizationStatus.SUCCESS.getCode()))
                .findAny()
                .orElseThrow(() -> new ServiceException("401", "인증되지 않았습니다.", HttpStatus.UNAUTHORIZED));

        Member member = memberRepository.findByEmailAndStatus(request.getEmail(), MemberStatusCode.NORMAL.getCode())
                .orElseThrow(() -> new ServiceException("404", "가입된 이메일을 찾을 수 없습니다."));

        member.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));

        memberRepository.save(member);
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setResponseCode("200");
        commonResponse.setDetailMessage("패스워드 변경에 성공했습니다.");
        commonResponse.setHttpStatus(HttpStatus.OK);

        return commonResponse;
    }

    private MimeMessage createPasswordKeyMimeMessage(String to, String generatedKey) throws MessagingException, IOException {

        MimeMessage message = emailSender.createMimeMessage();

        message.addRecipients(MimeMessage.RecipientType.TO, to);// 보내는 대상
        message.setSubject("Photory 비밀번호 인증 메일");// 제목

        ClassPathResource htmlSource = new ClassPathResource("/mail/password.html");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        htmlSource.getInputStream(),
                        StandardCharsets.UTF_8
                )
        );
        String msgg = reader.lines().collect(Collectors.joining(System.lineSeparator()));
//        String confirmLink = "https://127.0.0.1" + "/api/v1/auth/authorization/" + generatedKey + "/2"; // 메일에 인증번호 넣기
        String confirmLink = appConfig.getHost() + "/api/v1/auth/authorization/" + generatedKey + "/2"; // 메일에 인증번호 넣기
        msgg = msgg.replace("href=\"#\"", "href=" + confirmLink);
        message.setText(msgg, "utf-8", "html");// 내용, charset 타입, subtype
        // 보내는 사람의 이메일 주소, 보내는 사람 이름
        message.setFrom(new InternetAddress("no-reply@photory-app.com", "photory_admin"));// 보내는 사람


        return message;
    }

    public MemberInfoResponse getMyMemberInfo() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        val member = memberRepository.findByEmailAndStatus(user.getUsername(), MemberStatusCode.NORMAL.getCode()).orElseThrow(() -> new ServiceException("400", "no_data", HttpStatus.BAD_REQUEST));
        ReturnableMember returnableMember = new ReturnableMember();
        returnableMember.setEmail(member.getEmail());
        returnableMember.setNickName(member.getNickName());
        MemberInfoResponse memberInfoResponse = new MemberInfoResponse();
        memberInfoResponse.setMember(returnableMember);
        return memberInfoResponse;
    }

    public CommonResponse updateMemberInfo(MemberUpdateRequest memberUpdateRequest) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        val member = memberRepository.findByEmailAndStatus(user.getUsername(), MemberStatusCode.NORMAL.getCode()).orElseThrow(() -> new ServiceException("400", "no_data", HttpStatus.BAD_REQUEST));

        if (StringUtils.hasText(memberUpdateRequest.getPassword())) {
            member.setPassword(bCryptPasswordEncoder.encode(memberUpdateRequest.getPassword()));
        }
        if (StringUtils.hasText(memberUpdateRequest.getNickName())) {
            member.setNickName(memberUpdateRequest.getNickName());
        }
        memberRepository.save(member);
        return new CommonResponse();
    }

    public CommonResponse resignMember() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        val member = memberRepository.findByEmailAndStatus(user.getUsername(), MemberStatusCode.NORMAL.getCode()).orElseThrow(() -> new ServiceException("400", "no_data", HttpStatus.BAD_REQUEST));

        member.setStatus(MemberStatusCode.RESIGN.getCode());

        memberRepository.save(member);
        return new CommonResponse();
    }

    public MyDashboardResponse getUserDetailDashboard() {
        val user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        val member = memberRepository.findByEmailAndStatus(user.getUsername(), MemberStatusCode.NORMAL.getCode()).orElseThrow(() -> new ServiceException("400", "no_data", HttpStatus.BAD_REQUEST));
        val contents = challengeContentRepository.findByMemberSeq(member.getMemberSeq());
        MyDashboardResponse responseData = new MyDashboardResponse();
        contents.forEach(content -> {
            responseData.setTotalChallengeCount(responseData.getTotalChallengeCount() + 1);
            if (content.getContentsType().equals("tv")) {
                val tvData = responseData.getTv();
                tvData.setTotalCount(tvData.getTotalCount() + 1);
                tvData.setTotalRunTime(tvData.getTotalRunTime() + content.getRunTime().intValue());
                if (content.getIsDone()) {
                    tvData.setDoneCount(tvData.getDoneCount() + 1);
                    tvData.setDoneRunTime(tvData.getDoneRunTime() + content.getRunTime().intValue());
                }
            } else if (content.getContentsType().equals("movie")) {
                val movieData = responseData.getMovie();
                movieData.setTotalCount(movieData.getTotalCount() + 1);
                movieData.setTotalRunTime(movieData.getTotalRunTime() + content.getRunTime().intValue());
                if (content.getIsDone()) {
                    movieData.setDoneCount(movieData.getDoneCount() + 1);
                    movieData.setDoneRunTime(movieData.getDoneRunTime() + content.getRunTime().intValue());
                }
            }
        });
        // 일단 "구독" 업체만
        val records = challengeRecordRepository.findByMemberSeqAndType(member.getMemberSeq(), ChallengeCode.ProviderType.flatrate);
        records.forEach(record -> {
            val data = new ChallengeDashboardResponse.DataDashboard();
            data.setTotalCount(record.getTotalCount().intValue());
            data.setTotalRunTime(record.getTotalRunTime().intValue());
            data.setDoneCount(record.getDoneCount().intValue());
            data.setDoneRunTime(record.getDoneRunTime().intValue());
            responseData.getRecord().put(record.getProviderName(), data);
        });

        // 포토티켓 개수
        val photoTickets = photoTicketRepository.findByMemberSeq(member.getMemberSeq());
        responseData.setTotalPhotoTicketCount(photoTickets.size());
        return responseData;
    }
}
