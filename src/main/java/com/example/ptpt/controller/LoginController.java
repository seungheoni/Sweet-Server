package com.example.ptpt.controller;

import com.example.ptpt.delegator.AuthenticationDelegator;
import com.example.ptpt.dto.request.LoginRequest;
import com.example.ptpt.dto.response.TokenResponseDto;
import com.example.ptpt.security.UserAuthentication;
import com.example.ptpt.security.UserAuthenticationProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoginController {

    private final UserAuthenticationProvider userAuthenticationProvider;

//    private final AuthenticationDelegator authenticationDelegator;


    @Value("${jwt.signing-key}")
    private String jwtKey;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration; // 밀리초 단위, 예: 1800000 (30분)

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration; // 밀리초 단위, 예: 604800000 (7일)

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequest loginRequest) {

        System.out.println(loginRequest.getEmail());
        System.out.println(loginRequest.getPassword());

        try {
            String email = loginRequest.getEmail();
            String password = loginRequest.getPassword();

            UserAuthentication authentication = new UserAuthentication(email, password);
            userAuthenticationProvider.authenticate(authentication);

            SecretKey secretKey = Keys.hmacShaKeyFor(jwtKey.getBytes(StandardCharsets.UTF_8));

            Date now = new Date();
            Date accessTokenExpiryDate = new Date(now.getTime() + accessTokenExpiration);
//            String jwt = Jwts.builder()
//                    .claim("email", email)
//                    .signWith(secretKey)
//                    .compact();

            // Access Token 생성
            String accessToken = Jwts.builder()
                    .claim("email", email)
//                    .claim("username", username)
                    .issuedAt(now)
                    .expiration(accessTokenExpiryDate)
                    .signWith(secretKey)
                    .compact();

            // Refresh Token 만료 시간
            Date refreshTokenExpiryDate = new Date(now.getTime() + refreshTokenExpiration);

            // Refresh Token 생성
            String refreshToken = Jwts.builder()
                    .claim("email", email)
                    .issuedAt(now)
                    .expiration(refreshTokenExpiryDate)
                    .signWith(secretKey)
                    .compact();

            // 토큰 응답 DTO 생성
            TokenResponseDto tokenResponseDto = TokenResponseDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .accessTokenExpiresIn(accessTokenExpiration / 1000) // 초 단위로 변환
                    .refreshTokenExpiresIn(refreshTokenExpiration / 1000)
                    .build();


            System.out.println(tokenResponseDto.getAccessToken());
            System.out.println(tokenResponseDto.getRefreshToken());

            return ResponseEntity.ok(tokenResponseDto);

        } catch (Exception e) {
//            log.error("로그인 처리 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
}
