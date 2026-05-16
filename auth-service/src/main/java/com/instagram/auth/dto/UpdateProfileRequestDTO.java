package com.instagram.auth.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequestDTO {

    @Pattern(
            regexp = "^[A-Z][a-zA-Z]*(\\s[A-Z][a-zA-Z]*)*$",
            message = "Full name must contain only English letters, minimum 1 word, and each word must start with a capital letter"
    )
    private String fullName;

    @Size(max = 150, message = "Bio must not exceed 150 characters")
    private String bio;

    private String profilePicture;
}
