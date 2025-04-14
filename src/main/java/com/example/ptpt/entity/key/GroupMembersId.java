package com.example.ptpt.entity.key;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class GroupMembersId implements Serializable {
    private Long groupId;  // SocialGroups 엔티티의 ID
    private Long userId;
}