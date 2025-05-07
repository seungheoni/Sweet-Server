package com.example.ptpt.repository;

import com.example.ptpt.entity.Follows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowsRepository extends JpaRepository<Follows, Long> {
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);
    void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);
    Page<Follows> findByFollowingId(Long followingId, Pageable pageable);
    Page<Follows> findByFollowerId(Long followerId, Pageable pageable);
}