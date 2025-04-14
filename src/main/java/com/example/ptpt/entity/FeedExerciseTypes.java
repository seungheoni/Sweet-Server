package com.example.ptpt.entity;

import com.example.ptpt.entity.Feed;
import com.example.ptpt.entity.key.FeedExerciseTypesId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "feed_exercise_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedExerciseTypes {

    @EmbeddedId
    private FeedExerciseTypesId id;

    @MapsId("feedId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", nullable = false)
    private Feed feed;
}