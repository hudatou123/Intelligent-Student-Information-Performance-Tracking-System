package com.gradems.service;

import com.gradems.dto.request.TeacherRequest;
import com.gradems.dto.response.PageResponse;
import com.gradems.dto.response.TeacherResponse;
import com.gradems.entity.Teacher;
import com.gradems.entity.User;
import com.gradems.exception.DuplicateResourceException;
import com.gradems.exception.ResourceNotFoundException;
import com.gradems.repository.TeacherRepository;
import com.gradems.repository.UserRepository;
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
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public PageResponse<TeacherResponse> getAllTeachers(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fullName").ascending());

        Page<Teacher> teachers;
        if (StringUtils.hasText(search)) {
            teachers = teacherRepository
                    .findByFullNameContainingIgnoreCaseOrDepartmentContainingIgnoreCase(
                            search, search, pageable);
        } else {
            teachers = teacherRepository.findAll(pageable);
        }

        Page<TeacherResponse> responsePage = teachers.map(TeacherResponse::from);
        log.debug("Retrieved {} teachers (page {}/{})", teachers.getNumberOfElements(), page, teachers.getTotalPages());
        return PageResponse.from(responsePage);
    }

    @Transactional(readOnly = true)
    public TeacherResponse getTeacherById(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", id));
        return TeacherResponse.from(teacher);
    }

    @Transactional
    public TeacherResponse createTeacher(TeacherRequest request) {
        log.debug("Creating teacher with employeeId: {}", request.employeeId());

        if (teacherRepository.existsByEmployeeId(request.employeeId())) {
            throw new DuplicateResourceException("Teacher", "employeeId", request.employeeId());
        }

        User linkedUser = null;
        if (request.userId() != null) {
            linkedUser = userRepository.findById(request.userId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", request.userId()));
        }

        Teacher teacher = Teacher.builder()
                .employeeId(request.employeeId())
                .fullName(request.fullName())
                .department(request.department())
                .phone(request.phone())
                .user(linkedUser)
                .build();

        Teacher saved = teacherRepository.save(teacher);
        log.info("Created teacher: {} ({})", saved.getFullName(), saved.getEmployeeId());
        return TeacherResponse.from(saved);
    }

    @Transactional
    public TeacherResponse updateTeacher(Long id, TeacherRequest request) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", id));

        if (!teacher.getEmployeeId().equals(request.employeeId())
                && teacherRepository.existsByEmployeeId(request.employeeId())) {
            throw new DuplicateResourceException("Teacher", "employeeId", request.employeeId());
        }

        User linkedUser = null;
        if (request.userId() != null) {
            linkedUser = userRepository.findById(request.userId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", request.userId()));
        }

        teacher.setEmployeeId(request.employeeId());
        teacher.setFullName(request.fullName());
        teacher.setDepartment(request.department());
        teacher.setPhone(request.phone());
        teacher.setUser(linkedUser);

        Teacher updated = teacherRepository.save(teacher);
        log.info("Updated teacher: {} ({})", updated.getFullName(), updated.getEmployeeId());
        return TeacherResponse.from(updated);
    }

    @Transactional
    public void deleteTeacher(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", id));
        teacherRepository.delete(teacher);
        log.info("Deleted teacher: {} ({})", teacher.getFullName(), teacher.getEmployeeId());
    }

    @Transactional(readOnly = true)
    public long getTeacherCount() {
        return teacherRepository.count();
    }
}
