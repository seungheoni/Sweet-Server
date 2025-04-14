package com.example.ptpt.entity.key;

import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class FeedExerciseTypesId implements Serializable {
    private Long feedId;
    private String exerciseType;
}