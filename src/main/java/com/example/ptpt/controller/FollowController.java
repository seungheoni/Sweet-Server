package com.example.ptpt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "팔로우", description = "팔로우 관련 API")
@RestController
@RequestMapping("/api/follows")
public class FollowController {

    @Operation(summary = "팔로우 하기")
    @PostMapping("/{userId}")
    public ResponseEntity<Map<String, String>> follow(@PathVariable Long userId) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Successfully followed");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "언팔로우 하기")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, String>> unfollow(@PathVariable Long userId) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Successfully unfollowed");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "팔로워 목록 조회")
    @GetMapping("/followers")
    public ResponseEntity<Map<String, Object>> getFollowers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<Map<String, Object>> followers = Arrays.asList(
            createMockUser(1L),
            createMockUser(2L)
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", followers);
        response.put("totalPages", 1);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "팔로잉 목록 조회")
    @GetMapping("/following")
    public ResponseEntity<Map<String, Object>> getFollowing(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<Map<String, Object>> following = Arrays.asList(
            createMockUser(3L),
            createMockUser(4L)
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", following);
        response.put("totalPages", 1);
        
        return ResponseEntity.ok(response);
    }

    private Map<String, Object> createMockUser(Long id) {
        Map<String, Object> user = new HashMap<>();
        user.put("id", id);
        user.put("username", "user" + id);
        user.put("profileImage", "profile.jpg");
        return user;
    }
} 