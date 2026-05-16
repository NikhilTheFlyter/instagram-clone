package com.instagram.auth.dto;

import jakarta.validation.constraints.NotBlank;
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
public class ResetPasswordRequestDTO {

    @NotBlank(message = "Please provide a valid email")
    private String email;

    @NotBlank(message = "Please provide a valid resetToken")
    private String resetToken;

    @NotBlank(message = "Please provide a valid newPassword")
    @Size(min = 8, max = 16, message = "Password must be between 8 and 16 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$",
            message = "Password must contain at least one lowercase, one uppercase, one digit, and one special character"
    )
    private String newPassword;

    @NotBlank(message = "Please provide a valid confirmPassword")
    private String confirmPassword;
}
