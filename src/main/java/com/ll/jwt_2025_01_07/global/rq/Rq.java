package com.ll.jwt_2025_01_07.global.rq;

import com.ll.jwt_2025_01_07.domain.member.member.entity.Member;
import com.ll.jwt_2025_01_07.domain.member.member.service.MemberService;
import com.ll.jwt_2025_01_07.global.security.SecurityUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Arrays;
import java.util.Optional;

// Request/Response 를 추상화한 객체
// Request, Response, Cookie, Session 등을 다룬다.
@RequestScope
@Component
@RequiredArgsConstructor
public class Rq {
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final MemberService memberService;

    public void setLogin(Member member) {
        UserDetails user = new SecurityUser(
                member.getId(),
                member.getUsername(),
                "",
                member.getAuthorities()
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user,
                user.getPassword(),
                user.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public Member getActor() {
        return Optional.ofNullable(
                        SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                )
                .map(Authentication::getPrincipal)
                .filter(principal -> principal instanceof SecurityUser)
                .map(principal -> (SecurityUser) principal)
                .map(securityUser -> new Member(securityUser.getId(), securityUser.getUsername()))
                .orElse(null);
    }

    public void setCookie(String name, String value) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .path("/")
                .domain("localhost")
                .sameSite("Strict")
                .secure(true)
                .httpOnly(true)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    public String getCookieValue(String name) {
        return Optional
                .ofNullable(request.getCookies())
                .stream()
                .flatMap(cookies -> Arrays.stream(cookies))
                .filter(cookie -> cookie.getName().equals(name))
                .map(cookie -> cookie.getValue())
                .findFirst()
                .orElse(null);
    }

    public void setHeader(String name, String value) {
        response.setHeader(name, value);
    }

    public String getHeader(String name) {
        return response.getHeader(name);
    }

    public Optional<Member> findByActor() {
        Member actor = getActor();
        if (actor == null) {
            return Optional.empty();
        }
        return memberService.findById(actor.getId());
    }
}