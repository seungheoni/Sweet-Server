package com.example.ptpt.domain;

import com.example.ptpt.entity.Feed;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    private Long id;

    private String email;

    private String username;

    private String password;

    private String bio;

    private String profileImage;

    private List<Feed> feeds;
}
