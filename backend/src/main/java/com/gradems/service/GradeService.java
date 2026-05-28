package com.gradems.service;

import com.gradems.dto.request.GradeRequest;
import com.gradems.dto.response.GradeResponse;
import com.gradems.dto.response.PageResponse;
import com.gradems.entity.Grade;
import com.gradems.entity.Student;
import com.gradems.entity.Teacher;
import com.gradems.exception.ResourceNotFoundException;
import com.gradems.repository.GradeRepository;
import com.gradems.repository.StudentRepository;
import com.gradems.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GradeService {

    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    @Transactional(readOnly = true)
    public PageResponse<GradeResponse> getAllGrades(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Grade> grades = gradeRepository.findAll(pageable);
        Page<GradeResponse> responsePage = grades.map(GradeResponse::from);
        log.debug("Retrieved {} grades (page {}/{})", grades.getNumberOfElements(), page, grades.getTotalPages());
        return PageResponse.from(responsePage);
    }

    @Transactional(readOnly = true)
    public GradeResponse getGradeById(Long id) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grade", id));
        return GradeResponse.from(grade);
    }

    @Transactional(readOnly = true)
    public PageResponse<GradeResponse> getGradesByStudentId(Long studentId, int page, int size) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student", studentId);
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("semester").descending().and(Sort.by("courseName").ascending()));
        Page<Grade> grades = gradeRepository.findByStudentId(studentId, pageable);
        Page<GradeResponse> responsePage = grades.map(GradeResponse::from);
        return PageResponse.from(responsePage);
    }

    @Transactional
    public GradeResponse createGrade(GradeRequest request) {
        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", request.studentId()));

        Teacher teacher = null;
        if (request.teacherId() != null) {
            teacher = teacherRepository.findById(request.teacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher", request.teacherId()));
        }

        Grade grade = Grade.builder()
                .student(student)
                .teacher(teacher)
                .courseName(request.courseName())
                .semester(request.semester())
                .assignmentScore(request.assignmentScore())
                .midtermScore(request.midtermScore())
                .finalScore(request.finalScore())
                .build();

        // calculateGrades() is called via @PrePersist
        Grade saved = gradeRepository.save(grade);
        log.info("Created grade for student {} in course '{}' ({})",
                student.getStudentNumber(), request.courseName(), request.semester());
        return GradeResponse.from(saved);
    }

    @Transactional
    public GradeResponse updateGrade(Long id, GradeRequest request) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grade", id));

        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", request.studentId()));

        Teacher teacher = null;
        if (request.teacherId() != null) {
            teacher = teacherRepository.findById(request.teacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher", request.teacherId()));
        }

        grade.setStudent(student);
        grade.setTeacher(teacher);
        grade.setCourseName(request.courseName());
        grade.setSemester(request.semester());
        grade.setAssignmentScore(request.assignmentScore());
        grade.setMidtermScore(request.midtermScore());
        grade.setFinalScore(request.finalScore());

        // calculateGrades() is called via @PreUpdate
        Grade updated = gradeRepository.save(grade);
        log.info("Updated grade {} for student {} in course '{}'",
                id, student.getStudentNumber(), request.courseName());
        return GradeResponse.from(updated);
    }

    @Transactional
    public void deleteGrade(Long id) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grade", id));
        gradeRepository.delete(grade);
        log.info("Deleted grade {}", id);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getStudentGradeStats(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student", studentId);
        }

        Double average = gradeRepository.findAverageScoreByStudentId(studentId)
                .orElse(0.0);

        List<Object[]> distributionRows = gradeRepository.findGradeDistributionByStudentId(studentId);
        Map<String, Long> distribution = new LinkedHashMap<>();
        for (Object[] row : distributionRows) {
            String letterGrade = (String) row[0];
            Long count = (Long) row[1];
            distribution.put(letterGrade, count);
        }

        List<Grade> allGrades = gradeRepository.findByStudentId(studentId);
        long totalGrades = allGrades.size();

        Map<String, Object> stats = new HashMap<>();
        stats.put("studentId", studentId);
        stats.put("averageScore", Math.round(average * 100.0) / 100.0);
        stats.put("totalGrades", totalGrades);
        stats.put("gradeDistribution", distribution);

        return stats;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getCourseAverages() {
        List<Object[]> rows = gradeRepository.findAverageScoreByCourseName();
        return rows.stream().map(row -> {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("courseName", row[0]);
            double avg = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
            entry.put("averageScore", Math.round(avg * 100.0) / 100.0);
            return entry;
        }).toList();
    }
}
