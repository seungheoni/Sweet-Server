package com.example.ptpt.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- 기본 정보 ---
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, unique = true, length = 30)
    private String nickname;    // 이전 username → nickname

    @Column(length = 150)
    private String bio;

    @Column(name = "profile_image", length = 500)
    private String profileImage;  // 길이 확장

    @Column(length = 20)
    private String phoneNumber;   // 전화번호

    // --- 개인 정보 추가 ---
    @Column(name = "age_group", length = 20)
    private String ageGroup;      // 연령대 (10대, 20대, ...)

    @Column(length = 1)
    private String gender;        // 성별 (M, F)

    @Column(length = 50)
    private String region;        // 지역 (서울, 경기 등)

    @Column(name = "preferred_group_size")
    private Integer preferredGroupSize; // 선호 운동 그룹 인원

    // --- 인증 정보 ---
    @Column(nullable = true, length = 255)
    private String password;      // 소셜 가입 시 NULL 허용

    @Column(name = "social_id", length = 100)
    private String socialId;

    @Column(name = "social_type", length = 20)
    private String socialType;

    @Column(name = "social_profile_image", length = 500)
    private String socialProfileImage;

    // --- 계정 구분 및 상태 ---
    @Column(name = "user_type", nullable = false, length = 20)
    private String userType;         // NORMAL / SOCIAL

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "is_email_verified", nullable = false)
    private Boolean isEmailVerified;

    // --- 개인정보 동의 ---
    @Column(name = "agree_terms")
    private Boolean agreeTerms;

    @Column(name = "agree_privacy")
    private Boolean agreePrivacy;

    @Column(name = "agree_marketing")
    private Boolean agreeMarketing;

    // --- 마지막 로그인 ---
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "last_login_ip", length = 45)
    private String lastLoginIp;

    // --- 타임스탬프 ---
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // --- 관계 매핑 ---
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Feed> feeds;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
