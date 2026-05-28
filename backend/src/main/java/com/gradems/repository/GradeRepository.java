package com.gradems.repository;

import com.gradems.entity.Grade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    Page<Grade> findByStudentId(Long studentId, Pageable pageable);

    Page<Grade> findByTeacherId(Long teacherId, Pageable pageable);

    Page<Grade> findByStudentIdAndSemester(Long studentId, String semester, Pageable pageable);

    List<Grade> findByStudentId(Long studentId);

    @Query("SELECT AVG(g.totalScore) FROM Grade g WHERE g.student.id = :studentId")
    Optional<Double> findAverageScoreByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT g.courseName, AVG(g.totalScore) FROM Grade g GROUP BY g.courseName ORDER BY g.courseName")
    List<Object[]> findAverageScoreByCourseName();

    @Query("SELECT g.letterGrade, COUNT(g) FROM Grade g WHERE g.student.id = :studentId GROUP BY g.letterGrade ORDER BY g.letterGrade")
    List<Object[]> findGradeDistributionByStudentId(@Param("studentId") Long studentId);
}
