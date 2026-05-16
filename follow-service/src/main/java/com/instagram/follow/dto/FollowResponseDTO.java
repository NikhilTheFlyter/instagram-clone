package com.instagram.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowResponseDTO {

    private String followerId;
    private String followingId;
    private boolean isFollowing;
    private String message;
}
