package com.gradems.dto.response;

import com.gradems.entity.Student;

import java.time.LocalDateTime;

public record StudentResponse(
        Long id,
        String studentNumber,
        String firstName,
        String lastName,
        String fullName,
        String email,
        String phone,
        String address,
        Long userId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static StudentResponse from(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getStudentNumber(),
                student.getFirstName(),
                student.getLastName(),
                student.getFullName(),
                student.getEmail(),
                student.getPhone(),
                student.getAddress(),
                student.getUser() != null ? student.getUser().getId() : null,
                student.getCreatedAt(),
                student.getUpdatedAt()
        );
    }
}
