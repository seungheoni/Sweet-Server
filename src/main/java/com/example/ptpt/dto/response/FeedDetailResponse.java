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
@Schema(description = "피드 상세 응답 객체")
public class FeedDetailResponse {

    @Schema(description = "피드 ID", example = "1")
    private Long id;

    @Schema(description = "피드 제목", example = "오늘의 운동 일지")
    private String title;

    @Schema(description = "작성자 ID", example = "1")
    private Long authorId;

    @Schema(description = "작성자명", example = "홍길동")
    private String authorName;

    @Schema(description = "작성자 프로필 이미지 URL", example = "/profiles/images/user123.png")
    private String authorProfileImageUrl;

    @Schema(description = "첨부된 이미지 URL 목록", example = "[\"/feeds/images/img1.jpg\"]")
    private List<String> imageUrls;

    @Schema(description = "운동 이미지 URL", example = "/feeds/images/workout1.jpg")
    private String image;

    @Schema(description = "운동 종류", example = "러닝")
    private String exerciseType;

    @Schema(description = "운동 시간대", example = "아침")
    private String exerciseTime;

    @Schema(description = "운동 시간(분)", example = "30")
    private Integer workoutDuration;

    @Schema(description = "피드 본문 내용", example = "오늘 30분 운동했습니다.")
    private String feedContent;

    @Schema(description = "공개 범위", example = "공개")
    private FeedVisibility visibility;

    @Schema(description = "좋아요 수", example = "128")
    private Long likeCount;

    @Schema(description = "현재 사용자의 좋아요 여부", example = "true")
    private Boolean isLikedByCurrentUser;

    @Schema(description = "첫번째 좋아요 누른 유저명", example = "김철수")
    private String firstLikedUserName;

    @Schema(description = "첫번째 좋아요 누른 유저 프로필 이미지 URL", example = "/profiles/images/user123.png")
    private String firstLikedUserProfileImageUrl;

    @Schema(description = "댓글 수", example = "8")
    private Long commentCount;

    @Schema(description = "상위 댓글 목록")
    private List<CommentResponse> topComments;

    @Schema(description = "공유 수", example = "3")
    private Long shareCount;

    @Schema(description = "생성 시간(UTC)", example = "2025-04-21T23:32:00Z")
    private Instant createdAt;

    @Schema(description = "수정 시간(UTC)", example = "2025-04-21T23:35:00Z")
    private Instant updatedAt;
}