package com.example.ptpt.repository;

import com.example.ptpt.entity.Feed;
import com.example.ptpt.entity.FeedLikes;
import com.example.ptpt.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedLikeRepository extends JpaRepository<FeedLikes, Long> {
    boolean existsByFeedAndUser(Feed feed, Users user);
    void deleteByFeedIdAndUserId(Long feedId, Long userId);
}
