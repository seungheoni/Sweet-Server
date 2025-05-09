package com.example.ptpt.delegator;

// Auth service (인증 서버)를 호출하는 코드들이 존재

import com.example.ptpt.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class AuthenticationDelegator {
    private final RestTemplate restTemplate;

    @Value("${base-url.auth-server}")
    private String authServiceBaseUrl;

//    1번째 메서드: email, password 를 검증하는 api
    public void restAuth(String email, String password) {
        String url = authServiceBaseUrl + "/users/auth";

        User user = User.builder()
                .email(email)
                .password(password)
                .build();

        restTemplate.postForEntity(url, new HttpEntity<>(user), Void.class);
    }
}
