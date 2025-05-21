package com.example.ptpt.service;

import com.example.ptpt.dto.request.FeedRequest;
import com.example.ptpt.dto.response.FeedDetailResponse;
import com.example.ptpt.dto.response.FeedLikeResponse;
import com.example.ptpt.dto.response.FeedResponse;
import com.example.ptpt.enums.FeedType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FeedService {
    Page<FeedResponse> getFeeds(Pageable pageable,FeedType type);
    FeedDetailResponse getFeedById(Long id);
    FeedResponse createFeed(FeedRequest feedRequest);
    FeedResponse updateFeed(Long id, FeedRequest feedRequest);
    void deleteFeed(Long id);
    List<String> uploadImages(Long feedId, List<MultipartFile> files);

    /** 특정 사용자가 피드에 좋아요를 누릅니다. */
    void likeFeed(Long feedId, Long userId);

    /** 특정 사용자가 피드 좋아요를 취소합니다. */
    void unlikeFeed(Long feedId, Long userId);

    List<FeedLikeResponse> getFeedLikes(Long feedId);

}