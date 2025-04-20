package com.ll.jwt_2025_01_07.member.member.service;

import com.ll.jwt_2025_01_07.domain.member.member.entity.Member;
import com.ll.jwt_2025_01_07.domain.member.member.service.AuthTokenService;
import com.ll.jwt_2025_01_07.domain.member.member.service.MemberService;
import com.ll.jwt_2025_01_07.standard.util.Ut;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AuthTokenServiceTest {
    @Autowired
    private MemberService memberService;
    @Autowired
    private AuthTokenService authTokenService;

    private int expireSeconds = 60 * 60 * 24 * 365;

    private String secret = "abcdefghijklmnopqrstuvwxyz1234567890";

    @Test
    @DisplayName("authTokenService 가 존재한다.")
    void t1() {
        assertThat(authTokenService).isNotNull();
    }

    @Test
    @DisplayName("jjwt로 JWT 생성")
    void t2() {
        Date issuedAt = new Date();
        Date expiration = new Date(issuedAt.getTime() + 100L * expireSeconds);

        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes());

        Map<String, Object> payload = Map.of(
                "name", "Paul",
                "age", 23
        );

        String jwtStr = Jwts.builder()
                .claims(payload)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();

        assertThat(jwtStr).isNotNull();

        Map<String, Object> parsedPayload = (Map<String, Object>) Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parse(jwtStr)
                .getPayload();

        assertThat(parsedPayload)
                .containsAllEntriesOf(payload);
    }

    @Test
    @DisplayName("Ut.jwt.toString 으로 JWT 생성")
    void t3() {
        String jwtStr = Ut.jwt.toString(secret, expireSeconds, Map.of("name", "Paul", "age", 23));

        assertThat(jwtStr).isNotNull();

        assertThat(Ut.jwt.isValid(secret, jwtStr)).isTrue();
    }

    @Test
    @DisplayName("authTokenService.genAccessToken() 으로 JWT 생성")
    void t4() {
        Member memberUser1 = memberService.findByUsername("user1").get();

        String accessToken = authTokenService.genAccessToken(memberUser1);

        assertThat(accessToken).isNotNull();

        System.out.println("accessToken = " + accessToken);
    }
}
