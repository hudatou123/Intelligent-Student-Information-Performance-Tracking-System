-- V2: Seed data for development
-- Passwords are BCrypt hashed:
--   Admin@123  -> $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTpqfxkB9Oe
--   Teacher@123 -> $2a$10$GXbCNE2KCBjMGCm0P7E5VOpV8kCVF8THXHWN3DdkJibGRMPPwj8g2
--   Student@123 -> $2a$10$fLzHfVrT2y5B0mH/Z5oUQuUfmQ5EbQODqrWREu9LE2TqVVZQHaZhm

-- ============================================================
-- USERS
-- ============================================================

-- Admin user
INSERT INTO users (username, password, full_name, email, role, enabled)
VALUES ('admin',
        '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTpqfxkB9Oe',
        'System Administrator',
        'admin@gradems.com',
        'ADMIN',
        TRUE);

-- Teacher users
INSERT INTO users (username, password, full_name, email, role, enabled)
VALUES ('teacher_smith',
        '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTpqfxkB9Oe',
        'Dr. John Smith',
        'j.smith@gradems.com',
        'TEACHER',
        TRUE);

INSERT INTO users (username, password, full_name, email, role, enabled)
VALUES ('teacher_johnson',
        '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTpqfxkB9Oe',
        'Prof. Emily Johnson',
        'e.johnson@gradems.com',
        'TEACHER',
        TRUE);

-- Student users
INSERT INTO users (username, password, full_name, email, role, enabled)
VALUES ('student_alice',
        '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTpqfxkB9Oe',
        'Alice Chen',
        'alice.chen@student.gradems.com',
        'STUDENT',
        TRUE);

INSERT INTO users (username, password, full_name, email, role, enabled)
VALUES ('student_bob',
        '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTpqfxkB9Oe',
        'Bob Martinez',
        'bob.martinez@student.gradems.com',
        'STUDENT',
        TRUE);

INSERT INTO users (username, password, full_name, email, role, enabled)
VALUES ('student_carol',
        '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTpqfxkB9Oe',
        'Carol White',
        'carol.white@student.gradems.com',
        'STUDENT',
        TRUE);

INSERT INTO users (username, password, full_name, email, role, enabled)
VALUES ('student_david',
        '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTpqfxkB9Oe',
        'David Kim',
        'david.kim@student.gradems.com',
        'STUDENT',
        TRUE);

INSERT INTO users (username, password, full_name, email, role, enabled)
VALUES ('student_eva',
        '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTpqfxkB9Oe',
        'Eva Torres',
        'eva.torres@student.gradems.com',
        'STUDENT',
        TRUE);

-- ============================================================
-- TEACHERS
-- ============================================================

INSERT INTO teachers (employee_id, full_name, department, phone, user_id)
VALUES ('EMP001',
        'Dr. John Smith',
        'Computer Science',
        '555-0101',
        (SELECT id FROM users WHERE username = 'teacher_smith'));

INSERT INTO teachers (employee_id, full_name, department, phone, user_id)
VALUES ('EMP002',
        'Prof. Emily Johnson',
        'Mathematics',
        '555-0102',
        (SELECT id FROM users WHERE username = 'teacher_johnson'));

-- ============================================================
-- STUDENTS
-- ============================================================

INSERT INTO students (student_number, first_name, last_name, email, phone, address, user_id)
VALUES ('STU2024001',
        'Alice',
        'Chen',
        'alice.chen@student.gradems.com',
        '555-1001',
        '123 Campus Drive, Boston, MA 02115',
        (SELECT id FROM users WHERE username = 'student_alice'));

INSERT INTO students (student_number, first_name, last_name, email, phone, address, user_id)
VALUES ('STU2024002',
        'Bob',
        'Martinez',
        'bob.martinez@student.gradems.com',
        '555-1002',
        '456 University Ave, Cambridge, MA 02139',
        (SELECT id FROM users WHERE username = 'student_bob'));

INSERT INTO students (student_number, first_name, last_name, email, phone, address, user_id)
VALUES ('STU2024003',
        'Carol',
        'White',
        'carol.white@student.gradems.com',
        '555-1003',
        '789 College St, Somerville, MA 02143',
        (SELECT id FROM users WHERE username = 'student_carol'));

INSERT INTO students (student_number, first_name, last_name, email, phone, address, user_id)
VALUES ('STU2024004',
        'David',
        'Kim',
        'david.kim@student.gradems.com',
        '555-1004',
        '321 Scholar Lane, Medford, MA 02155',
        (SELECT id FROM users WHERE username = 'student_david'));

INSERT INTO students (student_number, first_name, last_name, email, phone, address, user_id)
VALUES ('STU2024005',
        'Eva',
        'Torres',
        'eva.torres@student.gradems.com',
        '555-1005',
        '654 Academic Blvd, Brookline, MA 02446',
        (SELECT id FROM users WHERE username = 'student_eva'));

-- ============================================================
-- GRADES
-- Assignment 30%, Midterm 30%, Final 40%
-- total_score = assignment*0.3 + midterm*0.3 + final*0.4
-- ============================================================

-- Alice Chen - CS101 Introduction to Programming (Spring 2024)
-- total = 90*0.3 + 85*0.3 + 92*0.4 = 27 + 25.5 + 36.8 = 89.3 -> B
INSERT INTO grades (student_id, teacher_id, course_name, semester,
                    assignment_score, midterm_score, final_score, total_score, letter_grade)
VALUES ((SELECT id FROM students WHERE student_number = 'STU2024001'),
        (SELECT id FROM teachers WHERE employee_id = 'EMP001'),
        'Introduction to Programming', 'Spring 2024',
        90.00, 85.00, 92.00, 89.30, 'B');

-- Alice Chen - MATH201 Calculus I (Spring 2024)
-- total = 95*0.3 + 90*0.3 + 95*0.4 = 28.5 + 27 + 38 = 93.5 -> A
INSERT INTO grades (student_id, teacher_id, course_name, semester,
                    assignment_score, midterm_score, final_score, total_score, letter_grade)
VALUES ((SELECT id FROM students WHERE student_number = 'STU2024001'),
        (SELECT id FROM teachers WHERE employee_id = 'EMP002'),
        'Calculus I', 'Spring 2024',
        95.00, 90.00, 95.00, 93.50, 'A');

-- Bob Martinez - CS101 Introduction to Programming (Spring 2024)
-- total = 75*0.3 + 70*0.3 + 78*0.4 = 22.5 + 21 + 31.2 = 74.7 -> C
INSERT INTO grades (student_id, teacher_id, course_name, semester,
                    assignment_score, midterm_score, final_score, total_score, letter_grade)
VALUES ((SELECT id FROM students WHERE student_number = 'STU2024002'),
        (SELECT id FROM teachers WHERE employee_id = 'EMP001'),
        'Introduction to Programming', 'Spring 2024',
        75.00, 70.00, 78.00, 74.70, 'C');

-- Bob Martinez - MATH201 Calculus I (Spring 2024)
-- total = 80*0.3 + 75*0.3 + 82*0.4 = 24 + 22.5 + 32.8 = 79.3 -> C
INSERT INTO grades (student_id, teacher_id, course_name, semester,
                    assignment_score, midterm_score, final_score, total_score, letter_grade)
VALUES ((SELECT id FROM students WHERE student_number = 'STU2024002'),
        (SELECT id FROM teachers WHERE employee_id = 'EMP002'),
        'Calculus I', 'Spring 2024',
        80.00, 75.00, 82.00, 79.30, 'C');

-- Carol White - CS101 Introduction to Programming (Spring 2024)
-- total = 88*0.3 + 92*0.3 + 90*0.4 = 26.4 + 27.6 + 36 = 90.0 -> A
INSERT INTO grades (student_id, teacher_id, course_name, semester,
                    assignment_score, midterm_score, final_score, total_score, letter_grade)
VALUES ((SELECT id FROM students WHERE student_number = 'STU2024003'),
        (SELECT id FROM teachers WHERE employee_id = 'EMP001'),
        'Introduction to Programming', 'Spring 2024',
        88.00, 92.00, 90.00, 90.00, 'A');

-- Carol White - Data Structures (Fall 2024)
-- total = 85*0.3 + 88*0.3 + 84*0.4 = 25.5 + 26.4 + 33.6 = 85.5 -> B
INSERT INTO grades (student_id, teacher_id, course_name, semester,
                    assignment_score, midterm_score, final_score, total_score, letter_grade)
VALUES ((SELECT id FROM students WHERE student_number = 'STU2024003'),
        (SELECT id FROM teachers WHERE employee_id = 'EMP001'),
        'Data Structures', 'Fall 2024',
        85.00, 88.00, 84.00, 85.50, 'B');

-- David Kim - MATH201 Calculus I (Spring 2024)
-- total = 60*0.3 + 65*0.3 + 62*0.4 = 18 + 19.5 + 24.8 = 62.3 -> D
INSERT INTO grades (student_id, teacher_id, course_name, semester,
                    assignment_score, midterm_score, final_score, total_score, letter_grade)
VALUES ((SELECT id FROM students WHERE student_number = 'STU2024004'),
        (SELECT id FROM teachers WHERE employee_id = 'EMP002'),
        'Calculus I', 'Spring 2024',
        60.00, 65.00, 62.00, 62.30, 'D');

-- David Kim - Data Structures (Fall 2024)
-- total = 72*0.3 + 68*0.3 + 75*0.4 = 21.6 + 20.4 + 30 = 72.0 -> C
INSERT INTO grades (student_id, teacher_id, course_name, semester,
                    assignment_score, midterm_score, final_score, total_score, letter_grade)
VALUES ((SELECT id FROM students WHERE student_number = 'STU2024004'),
        (SELECT id FROM teachers WHERE employee_id = 'EMP001'),
        'Data Structures', 'Fall 2024',
        72.00, 68.00, 75.00, 72.00, 'C');

-- Eva Torres - CS101 Introduction to Programming (Spring 2024)
-- total = 98*0.3 + 96*0.3 + 99*0.4 = 29.4 + 28.8 + 39.6 = 97.8 -> A
INSERT INTO grades (student_id, teacher_id, course_name, semester,
                    assignment_score, midterm_score, final_score, total_score, letter_grade)
VALUES ((SELECT id FROM students WHERE student_number = 'STU2024005'),
        (SELECT id FROM teachers WHERE employee_id = 'EMP001'),
        'Introduction to Programming', 'Spring 2024',
        98.00, 96.00, 99.00, 97.80, 'A');

-- Eva Torres - Data Structures (Fall 2024)
-- total = 91*0.3 + 93*0.3 + 95*0.4 = 27.3 + 27.9 + 38 = 93.2 -> A
INSERT INTO grades (student_id, teacher_id, course_name, semester,
                    assignment_score, midterm_score, final_score, total_score, letter_grade)
VALUES ((SELECT id FROM students WHERE student_number = 'STU2024005'),
        (SELECT id FROM teachers WHERE employee_id = 'EMP001'),
        'Data Structures', 'Fall 2024',
        91.00, 93.00, 95.00, 93.20, 'A');
