package com.example.ptpt.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "feed_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedImages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 여러 이미지가 하나의 피드에 연결됨 (ManyToOne)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", nullable = false)
    private Feed feed;

    @Column(name = "image_url", length = 500, nullable = false)
    private String imageUrl;
}