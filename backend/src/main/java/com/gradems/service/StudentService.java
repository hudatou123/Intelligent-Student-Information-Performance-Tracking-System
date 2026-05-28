package com.gradems.service;

import com.gradems.dto.request.StudentRequest;
import com.gradems.dto.response.PageResponse;
import com.gradems.dto.response.StudentResponse;
import com.gradems.entity.Student;
import com.gradems.exception.DuplicateResourceException;
import com.gradems.exception.ResourceNotFoundException;
import com.gradems.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {

    private final StudentRepository studentRepository;

    @Transactional(readOnly = true)
    public PageResponse<StudentResponse> getAllStudents(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastName", "firstName").ascending());

        Page<Student> students;
        if (StringUtils.hasText(search)) {
            students = studentRepository
                    .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                            search, search, pageable);
        } else {
            students = studentRepository.findAll(pageable);
        }

        Page<StudentResponse> responsePage = students.map(StudentResponse::from);
        log.debug("Retrieved {} students (page {}/{})", students.getNumberOfElements(), page, students.getTotalPages());
        return PageResponse.from(responsePage);
    }

    @Transactional(readOnly = true)
    public StudentResponse getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", id));
        return StudentResponse.from(student);
    }

    @Transactional
    public StudentResponse createStudent(StudentRequest request) {
        log.debug("Creating student with number: {}", request.studentNumber());

        if (studentRepository.existsByStudentNumber(request.studentNumber())) {
            throw new DuplicateResourceException("Student", "studentNumber", request.studentNumber());
        }

        if (StringUtils.hasText(request.email()) && studentRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Student", "email", request.email());
        }

        Student student = Student.builder()
                .studentNumber(request.studentNumber())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .phone(request.phone())
                .address(request.address())
                .build();

        Student saved = studentRepository.save(student);
        log.info("Created student: {} ({})", saved.getFullName(), saved.getStudentNumber());
        return StudentResponse.from(saved);
    }

    @Transactional
    public StudentResponse updateStudent(Long id, StudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", id));

        // Check for duplicate student number (only if changed)
        if (!student.getStudentNumber().equals(request.studentNumber())
                && studentRepository.existsByStudentNumber(request.studentNumber())) {
            throw new DuplicateResourceException("Student", "studentNumber", request.studentNumber());
        }

        // Check for duplicate email (only if changed)
        if (StringUtils.hasText(request.email())
                && !request.email().equals(student.getEmail())
                && studentRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Student", "email", request.email());
        }

        student.setStudentNumber(request.studentNumber());
        student.setFirstName(request.firstName());
        student.setLastName(request.lastName());
        student.setEmail(request.email());
        student.setPhone(request.phone());
        student.setAddress(request.address());

        Student updated = studentRepository.save(student);
        log.info("Updated student: {} ({})", updated.getFullName(), updated.getStudentNumber());
        return StudentResponse.from(updated);
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", id));
        studentRepository.delete(student);
        log.info("Deleted student: {} ({})", student.getFullName(), student.getStudentNumber());
    }

    @Transactional(readOnly = true)
    public long getStudentCount() {
        return studentRepository.count();
    }
}
