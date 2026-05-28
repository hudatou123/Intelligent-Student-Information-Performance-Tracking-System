package com.gradems.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TeacherRequest(
        @NotBlank(message = "Employee ID is required") String employeeId,
        @NotBlank(message = "Full name is required") String fullName,
        @NotBlank(message = "Department is required") String department,
        String phone,
        Long userId
) {}
