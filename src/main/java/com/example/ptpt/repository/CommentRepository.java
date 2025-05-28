package com.example.ptpt.repository;

import com.example.ptpt.entity.Comment;
import com.example.ptpt.entity.Feed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 해당 피드의 댓글을 생성일 기준 오름차순으로 가져오기
    Page<Comment> findByFeedOrderByCreatedAtAsc(Feed feed, Pageable pageable);
}