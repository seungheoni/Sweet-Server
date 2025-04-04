package com.example.fitmate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "포스트", description = "포스트 관련 API")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    @Operation(summary = "포스트 목록 조회")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<Map<String, Object>> posts = Arrays.asList(
            createMockPost(1L, "첫 번째 포스트", "오늘의 운동 기록"),
            createMockPost(2L, "두 번째 포스트", "헬스장 다녀왔어요")
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", posts);
        response.put("totalPages", 1);
        response.put("totalElements", posts.size());
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "포스트 상세 조회")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(createMockPost(id, "포스트 제목", "포스트 내용"));
    }

    @Operation(summary = "포스트 작성")
    @PostMapping
    public ResponseEntity<Map<String, Object>> createPost(@RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(createMockPost(1L, (String) request.get("title"), (String) request.get("content")));
    }

    @Operation(summary = "포스트 수정")
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updatePost(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(createMockPost(id, (String) request.get("title"), (String) request.get("content")));
    }

    @Operation(summary = "포스트 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletePost(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Post deleted successfully");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "이미지 업로드")
    @PostMapping("/images")
    public ResponseEntity<Map<String, List<String>>> uploadImages(@RequestParam("files") List<MultipartFile> files) {
        Map<String, List<String>> response = new HashMap<>();
        response.put("imageUrls", Arrays.asList("image1.jpg", "image2.jpg"));
        return ResponseEntity.ok(response);
    }

    private Map<String, Object> createMockPost(Long id, String title, String content) {
        Map<String, Object> post = new HashMap<>();
        post.put("id", id);
        post.put("title", title);
        post.put("content", content);
        post.put("imageUrls", Arrays.asList("image1.jpg", "image2.jpg"));
        post.put("author", createMockUser(1L));
        post.put("exerciseType", "WEIGHT_TRAINING");
        post.put("exerciseDetails", Map.of(
            "duration", 60,
            "calories", 500
        ));
        return post;
    }

    private Map<String, Object> createMockUser(Long id) {
        return Map.of(
            "id", id,
            "username", "user" + id,
            "profileImage", "profile.jpg"
        );
    }
} 