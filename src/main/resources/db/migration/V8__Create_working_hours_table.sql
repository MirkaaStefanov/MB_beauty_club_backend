-- V8__Create_working_hours_table.sql
CREATE TABLE working_hours (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    worker_id BINARY(16),
    day_of_week VARCHAR(20),
    start_time TIME,
    end_time TIME,
    is_deleted BOOLEAN DEFAULT FALSE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;