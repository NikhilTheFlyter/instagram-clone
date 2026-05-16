package com.instagram.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponseDTO {

    private String id;
    private String fullName;
    private String email;
    private String username;
    private String bio;
    private String profilePicture;
    private LocalDateTime createdAt;

    @Builder.Default
    private long postCount = 0;

    @Builder.Default
    private long followerCount = 0;

    @Builder.Default
    private long followingCount = 0;
}
