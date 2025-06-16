package com.example.ptpt.dto.response;

import com.example.ptpt.entity.Users;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "사용자 응답 객체")
public class UserResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @Schema(description = "이메일", example = "user@example.com")
    private String email;

    @Schema(description = "닉네임", example = "홍길동")
    private String nickname;

    @Schema(description = "사용자 소개", example = "안녕하세요, PTPT 사용자입니다.")
    private String bio;

    @Schema(
            description = "프로필 이미지 URL",
            example     = "/users/images/profile.png",
            defaultValue = ""
    )
    private String profileImageUrl;

    @Schema(
            description = "소셜 프로필 이미지 URL (백업용)",
            example     = "https://sns.com/avatar/123",
            defaultValue = ""
    )
    private String socialProfileImageUrl;

    @Schema(description = "전화번호", example = "010-1234-5678", defaultValue = "")
    private String phoneNumber;

    @Schema(description = "가입 타입", example = "NORMAL")
    private String userType;

    @Schema(description = "이메일 인증 완료 여부", example = "true")
    private Boolean isEmailVerified;

    // Entity → DTO 변환용 생성자
    public UserResponse(Users u) {
        this.id                     = u.getId();
        this.email                  = u.getEmail();
        this.nickname               = u.getNickname();
        this.bio                    = u.getBio();
        this.profileImageUrl        = u.getProfileImage();
        this.socialProfileImageUrl  = u.getSocialProfileImage();
        this.phoneNumber            = u.getPhoneNumber();
        this.userType               = u.getUserType();
        this.isEmailVerified        = u.getIsEmailVerified();
    }
}
