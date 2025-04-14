package com.example.ptpt.entity;

import com.example.ptpt.entity.key.FeedTagsId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "feed_tags")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedTags {

    @EmbeddedId
    private FeedTagsId id;

    @MapsId("feedId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", nullable = false)
    private Feed feed;
}
