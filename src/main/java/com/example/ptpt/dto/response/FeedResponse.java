package com.example.ptpt.dto.response;

import com.example.ptpt.enums.FeedVisibility;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "피드 목록 응답 객체")
public class FeedResponse {

    @Schema(description = "피드 ID", example = "1")
    private Long id;

    @Schema(description = "피드 제목", example = "오늘의 운동 일지")
    private String title;

    @Schema(description = "작성자 ID", example = "1")
    private Long authorId;

    @Schema(description = "작성자명", example = "홍길동")
    private String authorName;

    @Schema(description = "피드 전체 본문", example = "오늘 30분 운동했습니다. 기분이 너무 좋네요!")
    private String feedContent;

    @Schema(description = "작성자 프로필 이미지 URL", example = "/feeds/images/health.png")
    private String authorProfileImageUrl;

    @Schema(description = "이미지 URL 목록", example = "[\"/feeds/images/img1.jpg\"]")
    private List<String> imageUrls;

    @Schema(description = "피드 공개 범위", example = "public")
    private FeedVisibility visibility;

    @Schema(description = "좋아요 수", example = "128")
    private Long likeCount;

    @Schema(description = "현재 사용자가 좋아요를 눌렀는지 여부", example = "true")
    private Boolean isLikedByCurrentUser;

    @Schema(description = "첫번째 좋아요 누른 유저명", example = "김철수")
    private String firstLikedUserName;

    @Schema(description = "첫번째 좋아요 누른 유저 프로필 이미지 URL", example = "/profiles/images/user123.png")
    private String firstLikedUserProfileImageUrl;

    @Schema(description = "댓글 수", example = "8")
    private Long commentCount;

    @Schema(description = "게시글 공유 수", example = "3")
    private Long shareCount;

    @Schema(description = "작성 시간(UTC)", example = "2025-04-21T23:32:00")
    private Instant createdAt;

    @Schema(description = "수정 시간(UTC)", example = "2025-04-21T23:35:00")
    private Instant updatedAt;

    // ↓ 새로 추가된 운동 필드들
    @Schema(description = "운동 이미지 URL", example = "/feeds/images/run1.jpg")
    private String image;

    @Schema(description = "운동 종류", example = "러닝")
    private String exerciseType;

    @Schema(description = "운동 시간대", example = "아침")
    private String exerciseTime;

    @Schema(description = "운동 시간(분)", example = "30")
    private Integer workoutDuration;
}
