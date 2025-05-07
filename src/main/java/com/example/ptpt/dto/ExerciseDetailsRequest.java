package com.example.ptpt.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@Schema(description = "운동 상세 정보 데이터 전송 객체")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExerciseDetailsRequest {

    @Schema(
            description = "총 운동 시간 (예: \"30분\")",
            example = "30분",
            defaultValue = "30분"
    )
    private String duration;

    @Schema(
            description = "운동 타입 리스트 (예: \"workout\", \"running\", \"swimming\")",
            example = "[\"workout\", \"running\"]",
            defaultValue = "[\"workout\"]"
    )
    private List<String> exerciseType;

    @Schema(
            description = "운동 위치",
            example = "서울",
            defaultValue = "서울"
    )
    private String location;

    @Schema(
            description = "태그 (필요 시 확장 가능)",
            example = "피트니스",
            defaultValue = "피트니스"
    )
    private String tag;
}