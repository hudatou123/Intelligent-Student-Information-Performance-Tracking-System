package com.gradems.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "grades")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @Column(name = "course_name", nullable = false, length = 100)
    private String courseName;

    @Column(nullable = false, length = 20)
    private String semester;

    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @Column(name = "assignment_score")
    private Double assignmentScore;

    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @Column(name = "midterm_score")
    private Double midtermScore;

    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @Column(name = "final_score")
    private Double finalScore;

    @Column(name = "total_score")
    private Double totalScore;

    @Column(name = "letter_grade", length = 2)
    private String letterGrade;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void calculateGrades() {
        if (assignmentScore != null && midtermScore != null && finalScore != null) {
            totalScore = assignmentScore * 0.3 + midtermScore * 0.3 + finalScore * 0.4;
            letterGrade = computeLetterGrade(totalScore);
        }
    }

    private String computeLetterGrade(double score) {
        if (score >= 90) return "A";
        if (score >= 80) return "B";
        if (score >= 70) return "C";
        if (score >= 60) return "D";
        return "F";
    }
}
