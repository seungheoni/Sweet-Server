package com.example.ptpt.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "댓글 요청 DTO")
public class CommentRequest {
    @Schema(description = "댓글 내용", required = true)
    private String text;
}