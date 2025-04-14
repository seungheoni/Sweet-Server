package com.example.ptpt.service;

import com.example.ptpt.dto.request.FeedRequest;
import com.example.ptpt.dto.response.FeedDetailResponse;
import com.example.ptpt.dto.response.FeedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FeedService {
    Page<FeedResponse> getFeeds(Pageable pageable);
    FeedDetailResponse getFeedById(Long id);
    FeedResponse createFeed(FeedRequest feedRequest);
    FeedResponse updateFeed(Long id, FeedRequest feedRequest);
    void deleteFeed(Long id);
    List<String> uploadImages(Long feedId, List<MultipartFile> files);
}