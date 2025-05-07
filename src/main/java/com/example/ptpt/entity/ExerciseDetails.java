package com.example.ptpt.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exercise_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseDetails {

    @Id
    @Column(name = "feed_id")
    private Long feedId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "feed_id")
    private Feed feed;

    @Column(length = 50)
    private String duration;

    @Column(length = 255)
    private String location;
}
