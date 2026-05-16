package com.instagram.auth.dto;

import com.instagram.auth.validation.PasswordMatch;
import com.instagram.auth.validation.ValidEmailDomain;
import com.instagram.auth.validation.ValidFullName;
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
@PasswordMatch
public class RegisterRequestDTO {

    @NotBlank(message = "Please provide a valid fullName")
    @ValidFullName
    private String fullName;

    @NotBlank(message = "Please provide a valid email")
    @ValidEmailDomain
    private String email;

    @NotBlank(message = "Please provide a valid username")
    @Pattern(
            regexp = "^[a-z0-9._]+$",
            message = "Username should contain only lowercase letters, digits, and special characters (. _)"
    )
    private String username;

    @NotBlank(message = "Please provide a valid password")
    @Size(min = 8, max = 16, message = "Password must be between 8 and 16 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$",
            message = "Password must contain at least one lowercase, one uppercase, one digit, and one special character"
    )
    private String password;

    @NotBlank(message = "Please provide a valid confirmPassword")
    private String confirmPassword;
}
