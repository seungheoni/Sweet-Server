package com.example.ptpt.config;

import com.example.ptpt.security.filter.InitAuthFilter;
import com.example.ptpt.security.filter.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
//    private final InitAuthFilter initAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        System.out.println("요청 받음");

        httpSecurity.csrf(AbstractHttpConfigurer::disable);

//        httpSecurity.addFilterBefore(initAuthFilter, BasicAuthenticationFilter.class);
        httpSecurity.addFilterAfter(jwtAuthFilter, BasicAuthenticationFilter.class);

        httpSecurity.authorizeHttpRequests(c -> c
                .requestMatchers("/api/login").permitAll()
                .anyRequest().authenticated());
        return httpSecurity.build();
    }
}
