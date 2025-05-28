package com.example.ptpt.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "피드 좋아요 응답 DTO")
public class FeedLikeResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "사용자명", example = "john_doe")
    private String username;

    @Schema(description = "프로필 이미지 URL", example = "/feeds/images/john.png")
    private String profileImageUrl;

    @Schema(description = "좋아요 등록 시각(UTC)", example = "2025-05-21T06:30:00Z")
    private Instant likedAt;
}
