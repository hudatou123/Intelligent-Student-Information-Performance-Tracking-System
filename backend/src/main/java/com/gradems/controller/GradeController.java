package com.gradems.controller;

import com.gradems.dto.request.GradeRequest;
import com.gradems.dto.response.ApiResponse;
import com.gradems.dto.response.GradeResponse;
import com.gradems.dto.response.PageResponse;
import com.gradems.service.GradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
@Tag(name = "Grades", description = "Grade management endpoints")
public class GradeController {

    private final GradeService gradeService;

    @GetMapping
    @Operation(summary = "Get all grades", description = "Retrieve a paginated list of all grades")
    public ResponseEntity<ApiResponse<PageResponse<GradeResponse>>> getAllGrades(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        PageResponse<GradeResponse> grades = gradeService.getAllGrades(page, size);
        return ResponseEntity.ok(ApiResponse.success(grades));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get grade by ID")
    public ResponseEntity<ApiResponse<GradeResponse>> getGradeById(@PathVariable Long id) {
        GradeResponse grade = gradeService.getGradeById(id);
        return ResponseEntity.ok(ApiResponse.success(grade));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Create a new grade", description = "Admin or Teacher can create grades")
    public ResponseEntity<ApiResponse<GradeResponse>> createGrade(@Valid @RequestBody GradeRequest request) {
        GradeResponse grade = gradeService.createGrade(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Grade created successfully", grade));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Update a grade", description = "Admin or Teacher can update grades")
    public ResponseEntity<ApiResponse<GradeResponse>> updateGrade(
            @PathVariable Long id,
            @Valid @RequestBody GradeRequest request) {
        GradeResponse grade = gradeService.updateGrade(id, request);
        return ResponseEntity.ok(ApiResponse.success("Grade updated successfully", grade));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a grade", description = "Admin-only endpoint to delete a grade")
    public ResponseEntity<ApiResponse<Void>> deleteGrade(@PathVariable Long id) {
        gradeService.deleteGrade(id);
        return ResponseEntity.ok(ApiResponse.success("Grade deleted successfully", null));
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get grades by student ID", description = "Retrieve paginated grades for a specific student")
    public ResponseEntity<ApiResponse<PageResponse<GradeResponse>>> getGradesByStudent(
            @PathVariable Long studentId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        PageResponse<GradeResponse> grades = gradeService.getGradesByStudentId(studentId, page, size);
        return ResponseEntity.ok(ApiResponse.success(grades));
    }

    @GetMapping("/stats/student/{id}")
    @Operation(summary = "Get grade statistics for a student",
            description = "Returns average score and grade distribution for the student")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStudentStats(@PathVariable Long id) {
        Map<String, Object> stats = gradeService.getStudentGradeStats(id);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/stats/courses")
    @Operation(summary = "Get average scores per course",
            description = "Returns the average total score grouped by course name")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getCourseAverages() {
        List<Map<String, Object>> averages = gradeService.getCourseAverages();
        return ResponseEntity.ok(ApiResponse.success(averages));
    }
}
