-- V7__Create_vacations_table.sql
CREATE TABLE vacations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    worker_id BINARY(16),
    start_date DATE,
    end_date DATE,
    is_deleted BOOLEAN DEFAULT FALSE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;