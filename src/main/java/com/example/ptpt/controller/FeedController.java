package com.example.ptpt.controller;

import com.example.ptpt.dto.request.FeedRequest;
import com.example.ptpt.dto.response.FeedDetailResponse;
import com.example.ptpt.dto.response.FeedImageUploadResponse;
import com.example.ptpt.dto.response.FeedResponse;
import com.example.ptpt.enums.FeedType;
import com.example.ptpt.service.FeedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "피드", description = "피드 관련 API")
@RestController
@RequestMapping("/api/feeds")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @Operation(
            summary = "피드 목록 조회",
            description =
                    "페이징 응답(Page) 객체의 필수 정보:\n" +
                            "- **content**: 현재 페이지의 데이터 목록\n" +
                            "- **number**: 현재 페이지 번호 (0부터 시작)\n" +
                            "- **size**: 한 페이지당 항목 수\n" +
                            "- **totalElements**: 전체 데이터 개수\n" +
                            "- **totalPages**: 전체 페이지 수\n" +
                            "- **first**: 첫 페이지 여부\n" +
                            "- **last**: 마지막 페이지 여부"
    )
    @GetMapping
    public ResponseEntity<Page<FeedResponse>> getFeeds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) FeedType type) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FeedResponse> feeds = feedService.getFeeds(pageable,type);
        return ResponseEntity.ok(feeds);
    }

    @Operation(summary = "피드 상세 조회")
    @GetMapping("/{feedId}")
    public ResponseEntity<FeedDetailResponse> getFeedsDetail(@PathVariable Long feedId) {
        FeedDetailResponse feedDetailResponse = feedService.getFeedById(feedId);
        if (feedDetailResponse == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(feedDetailResponse);
    }

    @Operation(summary = "피드 작성")
    @PostMapping
    public ResponseEntity<FeedResponse> createPost(@RequestBody FeedRequest feedRequest) {
        FeedResponse createdFeed = feedService.createFeed(feedRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFeed);
    }

    @Operation(summary = "피드 수정")
    @PutMapping("/{feedId}")
    public ResponseEntity<FeedResponse> updatePost(
            @PathVariable Long feedId,
            @RequestBody FeedRequest feedRequest) {
        FeedResponse updatedFeed = feedService.updateFeed(feedId, feedRequest);
        if (updatedFeed == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedFeed);
    }

    @Operation(summary = "피드 삭제")
    @DeleteMapping("/{feedId}")
    public ResponseEntity<?> deletePost(@PathVariable Long feedId) {
        feedService.deleteFeed(feedId);
        return ResponseEntity.ok("{\"message\": \"feed deleted successfully\"}");
    }

    @Operation(summary = "피드 이미지 업로드", description = "여러 이미지를 업로드합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "업로드 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping(value = "/{feedId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FeedImageUploadResponse> uploadImages(@PathVariable Long feedId, @RequestParam("files") List<MultipartFile> files) {
        List<String> urls = feedService.uploadImages(feedId, files);
        return new ResponseEntity<>(new FeedImageUploadResponse(urls), HttpStatus.OK);
    }
}