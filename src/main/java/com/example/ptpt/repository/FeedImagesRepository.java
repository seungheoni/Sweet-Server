package com.example.ptpt.repository;

import com.example.ptpt.entity.Feed;
import com.example.ptpt.entity.FeedImages;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedImagesRepository extends JpaRepository<FeedImages,Long> {
    List<FeedImages> findByFeed(Feed feed);
}
