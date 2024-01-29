package com.ot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ot.component.JwtAuthenticationFilter;
import com.ot.component.JwtTokenProvider;
import com.ot.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    private final CustomUserDetailsService customUserDetailsService;

    private final ObjectMapper mainObjectMapper;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .formLogin().disable()
                .csrf().disable()
//                .rememberMe()
//                    .rememberMeParameter()
//                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .requestMatchers(matcher -> {
                    matcher.requestMatchers(
                    new AntPathRequestMatcher("/api/v1/challenge/**"),
                    new AntPathRequestMatcher("/api/v1/common/**"),
                    new AntPathRequestMatcher("/api/v1/photo-tickets/**"),
                    new AntPathRequestMatcher("/api/v1/search/**"),
                    new AntPathRequestMatcher("/api/v1/auth/user", HttpMethod.PUT.name()),
                    new AntPathRequestMatcher("/api/v1/auth/user", HttpMethod.DELETE.name()),
                    new AntPathRequestMatcher("/api/v1/auth/user/detail", HttpMethod.GET.name()),
                    new AntPathRequestMatcher("/api/v1/auth/user/detail/dashboard", HttpMethod.GET.name())
                    );
                })
                .authorizeRequests().anyRequest().authenticated()
                .and()
                .requiresChannel(channel ->
                        channel.anyRequest().requiresSecure())
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, mainObjectMapper), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setHideUserNotFoundExceptions(false);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(customUserDetailsService);
        return daoAuthenticationProvider;
    }

}
