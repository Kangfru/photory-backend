package com.ot.service;

import com.ot.code.MemberStatusCode;
import com.ot.exception.ServiceException;
import com.ot.repository.member.MemberRepository;
import com.ot.repository.member.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        val member = memberRepository.findByEmailAndStatus(email, MemberStatusCode.NORMAL.getCode()).orElseThrow(() -> new ServiceException("400", "no_data", HttpStatus.BAD_REQUEST));
        return createUserDetails(member);
    }

    private UserDetails createUserDetails(Member member) {
        // User Details
        return User.builder()
                .username(member.getUsername())
                .password(member.getPassword())
                .roles(member.getRoles().toArray(new String[0]))
                .build();
    }
}