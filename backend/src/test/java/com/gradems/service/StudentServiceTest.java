package com.gradems.service;

import com.gradems.dto.request.StudentRequest;
import com.gradems.dto.response.PageResponse;
import com.gradems.dto.response.StudentResponse;
import com.gradems.entity.Student;
import com.gradems.exception.DuplicateResourceException;
import com.gradems.exception.ResourceNotFoundException;
import com.gradems.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StudentService Unit Tests")
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private Student sampleStudent;
    private StudentRequest validRequest;

    @BeforeEach
    void setUp() {
        sampleStudent = Student.builder()
                .id(1L)
                .studentNumber("STU2024001")
                .firstName("Alice")
                .lastName("Chen")
                .email("alice@test.com")
                .phone("555-1001")
                .address("123 Test St")
                .build();

        validRequest = new StudentRequest(
                "STU2024001",
                "Alice",
                "Chen",
                "alice@test.com",
                "555-1001",
                "123 Test St"
        );
    }

    @Test
    @DisplayName("getAllStudents - returns paginated result when no search term provided")
    void getAllStudents_returnsPagedResult() {
        // Arrange
        List<Student> students = List.of(sampleStudent);
        Page<Student> studentPage = new PageImpl<>(students, PageRequest.of(0, 10), 1);

        when(studentRepository.findAll(any(Pageable.class))).thenReturn(studentPage);

        // Act
        PageResponse<StudentResponse> result = studentService.getAllStudents(0, 10, null);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1L);
        assertThat(result.page()).isEqualTo(0);
        assertThat(result.size()).isEqualTo(10);
        assertThat(result.content().get(0).studentNumber()).isEqualTo("STU2024001");
        assertThat(result.content().get(0).fullName()).isEqualTo("Alice Chen");

        verify(studentRepository, times(1)).findAll(any(Pageable.class));
        verify(studentRepository, never())
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                        anyString(), anyString(), any(Pageable.class));
    }

    @Test
    @DisplayName("createStudent - throws DuplicateResourceException when student number already exists")
    void createStudent_withDuplicateNumber_throwsDuplicateResourceException() {
        // Arrange
        when(studentRepository.existsByStudentNumber("STU2024001")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> studentService.createStudent(validRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("STU2024001");

        verify(studentRepository, times(1)).existsByStudentNumber("STU2024001");
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("getStudentById - throws ResourceNotFoundException when student does not exist")
    void getStudentById_withInvalidId_throwsResourceNotFoundException() {
        // Arrange
        Long nonExistentId = 999L;
        when(studentRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> studentService.getStudentById(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");

        verify(studentRepository, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("createStudent - successfully creates and returns student response")
    void createStudent_success_returnsStudentResponse() {
        // Arrange
        when(studentRepository.existsByStudentNumber("STU2024001")).thenReturn(false);
        when(studentRepository.existsByEmail("alice@test.com")).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(sampleStudent);

        // Act
        StudentResponse result = studentService.createStudent(validRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.studentNumber()).isEqualTo("STU2024001");
        assertThat(result.firstName()).isEqualTo("Alice");
        assertThat(result.lastName()).isEqualTo("Chen");
        assertThat(result.fullName()).isEqualTo("Alice Chen");
        assertThat(result.email()).isEqualTo("alice@test.com");

        verify(studentRepository, times(1)).existsByStudentNumber("STU2024001");
        verify(studentRepository, times(1)).existsByEmail("alice@test.com");
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    @DisplayName("getAllStudents - uses search query when search term is provided")
    void getAllStudents_withSearchTerm_usesSearchQuery() {
        // Arrange
        List<Student> students = List.of(sampleStudent);
        Page<Student> studentPage = new PageImpl<>(students, PageRequest.of(0, 10), 1);

        when(studentRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                eq("Alice"), eq("Alice"), any(Pageable.class))).thenReturn(studentPage);

        // Act
        PageResponse<StudentResponse> result = studentService.getAllStudents(0, 10, "Alice");

        // Assert
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).firstName()).isEqualTo("Alice");

        verify(studentRepository, times(1))
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                        eq("Alice"), eq("Alice"), any(Pageable.class));
        verify(studentRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("deleteStudent - throws ResourceNotFoundException when student does not exist")
    void deleteStudent_withInvalidId_throwsResourceNotFoundException() {
        // Arrange
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> studentService.deleteStudent(999L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(studentRepository, never()).delete(any(Student.class));
    }
}
