package com.example.ptpt.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "팔로우 상태 응답 객체")
public class FollowStatusResponse {

    @Schema(description = "팔로우 상태 (true면 팔로우 중)", example = "true")
    private Boolean following;
}