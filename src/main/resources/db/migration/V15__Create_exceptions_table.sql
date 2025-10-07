-- V15__Create_exceptions_table.sql
CREATE TABLE exceptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    status_code INT,
    exception_type VARCHAR(255),
    exception_message TEXT,
    stack_trace_string TEXT,
    method_name VARCHAR(255),
    class_name VARCHAR(255),
    line_number INT,
    severity VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;