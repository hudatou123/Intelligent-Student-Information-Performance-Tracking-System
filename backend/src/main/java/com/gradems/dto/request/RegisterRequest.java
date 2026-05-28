package com.gradems.dto.request;

import com.gradems.entity.Role;
import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        @NotBlank(message = "Full name is required")
        String fullName,

        @Email(message = "Email must be valid")
        String email,

        @NotNull(message = "Role is required")
        Role role
) {}
