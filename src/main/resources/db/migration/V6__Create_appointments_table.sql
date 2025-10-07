-- V6__Create_appointments_table.sql
CREATE TABLE appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    worker_id BINARY(16),
    user_id BIGINT,
    username VARCHAR(255),
    phone_number VARCHAR(50),
    service_id BINARY(16),
    start_time DATETIME,
    end_time DATETIME,
    status VARCHAR(50) DEFAULT 'PENDING',
    is_deleted BOOLEAN DEFAULT FALSE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;