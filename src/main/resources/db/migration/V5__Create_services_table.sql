-- V5__Create_services_table.sql
CREATE TABLE services (
    id BINARY(16) PRIMARY KEY,
    category VARCHAR(50),
    name VARCHAR(255),
    description TEXT,
    price DOUBLE,
    duration INT,
    is_deleted BOOLEAN DEFAULT FALSE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;