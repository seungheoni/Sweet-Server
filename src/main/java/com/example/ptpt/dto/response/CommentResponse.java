package com.example.ptpt.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "댓글 응답 객체")
public class CommentResponse {
    @Schema(description = "댓글 ID", example = "15")
    private Long commentId;

    @Schema(description = "작성자 ID", example = "2")
    private Long userId;

    @Schema(description = "작성자명", example = "chimchakman_")
    private String userName;

    @Schema(description = "프로필 사진 URL", example = "https://cdn.example.com/profiles/2.png")
    private String profileImageUrl;    // 새로 추가된 필드

    @Schema(description = "댓글 내용", example = "소통해요~")
    private String text;

    @Schema(description = "댓글 작성 시간(UTC)", example = "2025-04-22T09:15:00Z")
    private Instant createdAt;
}