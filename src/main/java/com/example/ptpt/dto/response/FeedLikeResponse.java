package com.example.ptpt.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedLikeResponse {
    private Long userId;
    private String username;
    private String profileImageUrl;
    private LocalDateTime likedAt;
}