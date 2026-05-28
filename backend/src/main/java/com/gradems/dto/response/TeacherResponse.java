package com.gradems.dto.response;

import com.gradems.entity.Teacher;

import java.time.LocalDateTime;

public record TeacherResponse(
        Long id,
        String employeeId,
        String fullName,
        String department,
        String phone,
        Long userId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TeacherResponse from(Teacher teacher) {
        return new TeacherResponse(
                teacher.getId(),
                teacher.getEmployeeId(),
                teacher.getFullName(),
                teacher.getDepartment(),
                teacher.getPhone(),
                teacher.getUser() != null ? teacher.getUser().getId() : null,
                teacher.getCreatedAt(),
                teacher.getUpdatedAt()
        );
    }
}
