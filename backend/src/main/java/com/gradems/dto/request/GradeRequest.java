package com.gradems.dto.request;

import jakarta.validation.constraints.*;

public record GradeRequest(
        @NotNull(message = "Student ID is required") Long studentId,
        Long teacherId,
        @NotBlank(message = "Course name is required") String courseName,
        @NotBlank(message = "Semester is required") String semester,
        @NotNull(message = "Assignment score is required")
        @DecimalMin(value = "0.0", message = "Assignment score must be at least 0")
        @DecimalMax(value = "100.0", message = "Assignment score must be at most 100")
        Double assignmentScore,
        @NotNull(message = "Midterm score is required")
        @DecimalMin(value = "0.0", message = "Midterm score must be at least 0")
        @DecimalMax(value = "100.0", message = "Midterm score must be at most 100")
        Double midtermScore,
        @NotNull(message = "Final score is required")
        @DecimalMin(value = "0.0", message = "Final score must be at least 0")
        @DecimalMax(value = "100.0", message = "Final score must be at most 100")
        Double finalScore
) {}
