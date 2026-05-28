package com.gradems.repository;

import com.gradems.entity.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    Optional<Teacher> findByEmployeeId(String employeeId);

    Optional<Teacher> findByUserId(Long userId);

    Page<Teacher> findByFullNameContainingIgnoreCaseOrDepartmentContainingIgnoreCase(
            String name, String dept, Pageable pageable);

    boolean existsByEmployeeId(String employeeId);
}
