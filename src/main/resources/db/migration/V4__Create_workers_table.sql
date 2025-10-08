-- V4__Create_workers_table.sql
CREATE TABLE workers (
    id BINARY(16) PRIMARY KEY,
    user_id BINARY(16),
    name VARCHAR(255),
    email VARCHAR(255),
    worker_category VARCHAR(50),
    is_deleted BOOLEAN DEFAULT FALSE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;