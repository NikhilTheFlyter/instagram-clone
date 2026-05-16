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
public class UserResponseDTO {

    private String id;
    private String fullName;
    private String email;
    private String username;
    private String profilePicture;
    private String bio;
    private LocalDateTime createdAt;
}
