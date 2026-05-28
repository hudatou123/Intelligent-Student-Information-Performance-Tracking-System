-- V3: Fix BCrypt password hashes
-- Admin@123
UPDATE users SET password = '$2b$10$YW9W3yqwKBLPQ97RnhBqBetPh16LCxXeVAZY65oiBVg8VCKpr7qIO'
WHERE username = 'admin';

-- Teacher@123
UPDATE users SET password = '$2b$10$On5GshgFZtObBQibhOKvdefahXM2JRosjnW4ENI4G6m6ylJ0S67zC'
WHERE role = 'TEACHER';

-- Student@123
UPDATE users SET password = '$2b$10$aFzvUoFAbtaNy8OoP7/gluOnvr0SfPC04ozkj2Efq/qtXaYFvD9ka'
WHERE role = 'STUDENT';
