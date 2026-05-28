package com.gradems.dto.response;

import com.gradems.entity.Grade;

import java.time.LocalDateTime;

public record GradeResponse(
        Long id,
        Long studentId,
        String studentName,
        String studentNumber,
        Long teacherId,
        String teacherName,
        String courseName,
        String semester,
        Double assignmentScore,
        Double midtermScore,
        Double finalScore,
        Double totalScore,
        String letterGrade,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static GradeResponse from(Grade grade) {
        String studentName = grade.getStudent() != null ? grade.getStudent().getFullName() : null;
        String studentNumber = grade.getStudent() != null ? grade.getStudent().getStudentNumber() : null;
        Long teacherId = grade.getTeacher() != null ? grade.getTeacher().getId() : null;
        String teacherName = grade.getTeacher() != null ? grade.getTeacher().getFullName() : null;

        return new GradeResponse(
                grade.getId(),
                grade.getStudent() != null ? grade.getStudent().getId() : null,
                studentName,
                studentNumber,
                teacherId,
                teacherName,
                grade.getCourseName(),
                grade.getSemester(),
                grade.getAssignmentScore(),
                grade.getMidtermScore(),
                grade.getFinalScore(),
                grade.getTotalScore(),
                grade.getLetterGrade(),
                grade.getCreatedAt(),
                grade.getUpdatedAt()
        );
    }
}
