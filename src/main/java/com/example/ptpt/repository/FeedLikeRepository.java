package com.example.ptpt.repository;

import com.example.ptpt.entity.Feed;
import com.example.ptpt.entity.FeedLikes;
import com.example.ptpt.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeedLikeRepository extends JpaRepository<FeedLikes, Long> {
    boolean existsByFeedAndUser(Feed feed, UserEntity user);
    void deleteByFeedIdAndUserId(Long feedId, Long userId);


    // 좋아요 수 집계
    long countByFeed(Feed feed);

    // 최초 좋아요 한 사용자 조회 (등록일 기준 오름차순)
    Optional<FeedLikes> findFirstByFeedOrderByCreatedAtAsc(Feed feed);

    // 기존 메서드 외에
    List<FeedLikes> findByFeedOrderByCreatedAtDesc(Feed feed);
}
