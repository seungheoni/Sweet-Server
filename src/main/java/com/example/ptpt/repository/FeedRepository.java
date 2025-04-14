package com.example.ptpt.repository;

import com.example.ptpt.entity.Feed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedRepository extends JpaRepository<Feed, Long> {
    // 기본적으로 JpaRepository에서 제공하는 findAll(Pageable pageable) 메서드 사용
    Page<Feed> findAll(Pageable pageable);
}