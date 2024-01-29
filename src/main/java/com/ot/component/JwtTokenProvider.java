package com.ot.component;

import com.ot.code.MemberStatusCode;
import com.ot.model.auth.TokenInfo;
import com.ot.repository.member.MemberRepository;
import com.ot.repository.member.RefreshTokenRepository;
import com.ot.repository.member.entity.Member;
import com.ot.repository.member.entity.RefreshToken;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProvider {
    private final RefreshTokenRepository refreshTokenRepository;

    private final long ONE_DAY_AS_MILLISECONDS = 86400 * 1000;

    private final Key key;

    private final MemberRepository memberRepository;


    public JwtTokenProvider(@Value("${jwt.secret}") String key,
                            RefreshTokenRepository refreshTokenRepository,
                            MemberRepository memberRepository) {
        byte[] keyBytes = Decoders.BASE64.decode(key);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.refreshTokenRepository = refreshTokenRepository;
        this.memberRepository = memberRepository;
    }

    // 유저 정보를 가지고 AccessToken, RefreshToken 을 생성하는 메서드
    public TokenInfo generateToken(Authentication authentication, boolean isRememberMe) {
        // 권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + ONE_DAY_AS_MILLISECONDS);
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        long refreshTokenExpiration = ONE_DAY_AS_MILLISECONDS;
        if (isRememberMe) {
            refreshTokenExpiration *= 365L;
        }

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + refreshTokenExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        if (refreshTokenRepository.existsByEmail(authentication.getName())){
            refreshTokenRepository.deleteByEmail(authentication.getName());
        }

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .email(authentication.getName())
                .refreshToken(refreshToken)
                .build();

        refreshTokenRepository.save(refreshTokenEntity);

        TokenInfo tokenInfo = TokenInfo.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        return tokenInfo;
    }

    // JWT 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {
        // 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }
    // 토큰 정보를 검증하는 메서드
    public void validateToken(String token) throws Exception {
        Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    public String validateRefreshToken(String refreshToken) throws Exception {

        // 검증
        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(refreshToken); // parseClaimsJws 에서 만료 등일 때 exception 발생 함

        //refresh 토큰의 만료시간이 지나지 않았을 경우, 새로운 access 토큰을 생성합니다.
        return regenerateAccessToken(refreshToken);
    }

    public String regenerateAccessToken(String refreshToken) throws Exception {

        RefreshToken refreshTokenEntity = refreshTokenRepository.findByRefreshToken(refreshToken).orElseThrow(() -> new MalformedJwtException("유효하지 않은 토큰입니다."));

        Member member = memberRepository.findByEmailAndStatus(refreshTokenEntity.getEmail(), MemberStatusCode.NORMAL.getCode())
                .orElseThrow(() -> new MalformedJwtException("유효하지 않은 토큰입니다."));

        long now = (new Date()).getTime();
        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + ONE_DAY_AS_MILLISECONDS);

        //Access Token
        String accessToken = Jwts.builder()
                .setSubject(member.getEmail())
                .claim("auth", String.join(",", member.getRoles()))
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return accessToken;
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }


}
