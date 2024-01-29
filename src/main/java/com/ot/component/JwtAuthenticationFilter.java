package com.ot.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ot.model.CommonResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    private final ObjectMapper mainObjectMapper;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        // 1. Request Header 에서 JWT 토큰 추출
        String token = resolveToken((HttpServletRequest) request);

        // 2. validateToken 으로 토큰 유효성 검사
        try {
            jwtTokenProvider.validateToken(token);
        } catch (Exception e) {
            // 바로 오류 응답.
            CommonResponse commonResponse = new CommonResponse();
            commonResponse.setHttpStatus(HttpStatus.FORBIDDEN);
            if (e instanceof ExpiredJwtException) {
                commonResponse.setResponseCode("4031");
                commonResponse.setResponseMessage("만료된 토큰입니다.");
                commonResponse.setDetailMessage("만료된 토큰입니다.");
            } else if (e instanceof io.jsonwebtoken.security.SecurityException || e instanceof MalformedJwtException) {
                commonResponse.setResponseCode("4032");
                commonResponse.setResponseMessage("유효하지 않은 토큰입니다.");
            } else {
                commonResponse.setResponseCode("403");
                commonResponse.setResponseMessage("Forbidden");
            }
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpResponse.setContentType("application/json;charset=UTF-8");
            httpResponse.getWriter().write(mainObjectMapper.writeValueAsString(commonResponse));
            return;
        }
        if (token != null) {
            // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext 에 저장
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }

    // Request Header 에서 토큰 정보 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}