package com.example.ptpt.security.filter;

import com.example.ptpt.controller.LoginController;
import com.example.ptpt.dto.request.LoginRequest;
import com.example.ptpt.security.UserAuthentication;
import com.example.ptpt.security.UserAuthenticationProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
//@Component
@RequiredArgsConstructor
public class InitAuthFilter extends OncePerRequestFilter {

    private final UserAuthenticationProvider userAuthenticationProvider;
    private final ObjectMapper objectMapper;

    @Value("${jwt.signing-key}")
    private String jwtKey;

//    login 일 때 이 filter 사용
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

//        LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
//
//        String email = loginRequest.getEmail();
//        String password = loginRequest.getPassword();
//
//        log.info("Username: {}", email);
//        log.info("Password: {}", password);
//
//        UserAuthentication authentication = new UserAuthentication(email, password);
//        userAuthenticationProvider.authenticate(authentication);
//
//
//        SecretKey secretKey = Keys.hmacShaKeyFor(jwtKey.getBytes(StandardCharsets.UTF_8));
//
//        String jwt = Jwts.builder()
//                .claim("email", email)
//                .signWith(secretKey)
//                .compact();
//
//        response.setHeader("Authorization", jwt);
//
//        filterChain.doFilter(request, response);
//    }

//    login 이 아닐때는 이 필터를 사용하지 않겠다.
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        System.out.println("InitAuth shouldNotFilter");


        return !request.getServletPath().equals("/api/login");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
        System.out.println("InitAuth doFilter");



    }
}
