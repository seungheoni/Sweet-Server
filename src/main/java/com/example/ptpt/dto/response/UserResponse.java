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

    @Schema(description = "사용자명", example = "홍길동")
    private String username;

    @Schema(
            description = "프로필 이미지 URL",
            example = "/users/images/profile.png",
            defaultValue = ""
    )
    private String profileImageUrl;

    // User → DTO 변환용 생성자
    public UserResponse(Users u) {
        this.id              = u.getId();
        this.username        = u.getUsername();
        this.profileImageUrl = u.getProfileImage();
    }
}