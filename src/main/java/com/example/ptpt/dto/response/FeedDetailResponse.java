package com.example.ptpt.dto.response;

import com.example.ptpt.dto.ExerciseDetailsRequest;
import com.example.ptpt.enums.FeedVisibility;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "피드 데이터 응답 객체")
public class FeedDetailResponse {

    @Schema(description = "피드 ID", example = "1")
    private Long id;

    @Schema(description = "피드 제목", example = "오늘의 운동 일지", defaultValue = "기본 제목")
    private String title;

    @Schema(description = "피드 내용", example = "오늘 30분 운동했습니다. 기분이 좋네요.", defaultValue = "기본 내용")
    private String content;

    @Schema(description = "작성자 ID", example = "1", defaultValue = "1")
    private Long authorId;

    @Schema(description = "이미지 URL 목록",
            example = "[\"https://ptpt.com/images/img1.jpg\", \"https://ptpt.com/images/img2.jpg\"]")
    private List<String> imageUrls;

    @Schema(description = "피드 공개 범위", example = "공개", defaultValue = "공개")
    private FeedVisibility visibility;

    @Schema(description = "운동 상세 정보")
    private ExerciseDetailsRequest exerciseDetails;
}