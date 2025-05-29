package com.example.ptpt.enums;

import io.swagger.v3.oas.annotations.media.Schema;

public enum FeedType {
    @Schema(description = "내가 팔로우한 사람들의 피드만 조회")
    FOLLOWING,   // 내가 팔로우한 사람들의 피드
    @Schema(description = "내가 팔로우하지 않은 사람들의 피드만 조회")
    UNFOLLOWED    // 내가 팔로우하지 않은 사람들
}