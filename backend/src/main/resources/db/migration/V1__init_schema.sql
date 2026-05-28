-- V1: Initial schema for Grade Management System

CREATE TABLE users (
    id         BIGSERIAL PRIMARY KEY,
    username   VARCHAR(50)  UNIQUE NOT NULL,
    password   VARCHAR(255) NOT NULL,
    full_name  VARCHAR(100) NOT NULL,
    email      VARCHAR(100) UNIQUE,
    role       VARCHAR(20)  NOT NULL CHECK (role IN ('ADMIN', 'TEACHER', 'STUDENT')),
    enabled    BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE teachers (
    id          BIGSERIAL PRIMARY KEY,
    employee_id VARCHAR(20)  UNIQUE NOT NULL,
    full_name   VARCHAR(100) NOT NULL,
    department  VARCHAR(100) NOT NULL,
    phone       VARCHAR(20),
    user_id     BIGINT REFERENCES users (id) ON DELETE SET NULL,
    created_at  TIMESTAMP DEFAULT NOW(),
    updated_at  TIMESTAMP DEFAULT NOW()
);

CREATE TABLE students (
    id             BIGSERIAL PRIMARY KEY,
    student_number VARCHAR(20)  UNIQUE NOT NULL,
    first_name     VARCHAR(50)  NOT NULL,
    last_name      VARCHAR(50)  NOT NULL,
    email          VARCHAR(100) UNIQUE,
    phone          VARCHAR(20),
    address        TEXT,
    user_id        BIGINT REFERENCES users (id) ON DELETE SET NULL,
    created_at     TIMESTAMP DEFAULT NOW(),
    updated_at     TIMESTAMP DEFAULT NOW()
);

CREATE TABLE grades (
    id               BIGSERIAL PRIMARY KEY,
    student_id       BIGINT        NOT NULL REFERENCES students (id) ON DELETE CASCADE,
    teacher_id       BIGINT        REFERENCES teachers (id) ON DELETE SET NULL,
    course_name      VARCHAR(100)  NOT NULL,
    semester         VARCHAR(20)   NOT NULL,
    assignment_score DOUBLE PRECISION CHECK (assignment_score BETWEEN 0 AND 100),
    midterm_score    DOUBLE PRECISION CHECK (midterm_score BETWEEN 0 AND 100),
    final_score      DOUBLE PRECISION CHECK (final_score BETWEEN 0 AND 100),
    total_score      DOUBLE PRECISION,
    letter_grade     VARCHAR(2),
    created_at       TIMESTAMP DEFAULT NOW(),
    updated_at       TIMESTAMP DEFAULT NOW()
);

-- Indexes for performance
CREATE INDEX idx_users_username ON users (username);
CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_students_student_number ON students (student_number);
CREATE INDEX idx_students_email ON students (email);
CREATE INDEX idx_teachers_employee_id ON teachers (employee_id);
CREATE INDEX idx_grades_student_id ON grades (student_id);
CREATE INDEX idx_grades_teacher_id ON grades (teacher_id);
CREATE INDEX idx_grades_semester ON grades (semester);
CREATE INDEX idx_grades_course_name ON grades (course_name);
