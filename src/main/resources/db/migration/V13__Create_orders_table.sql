-- V13__Create_orders_table.sql
CREATE TABLE orders (
    id BINARY(16) PRIMARY KEY,
    user_id BINARY(16),
    order_date DATE,
    is_deleted BOOLEAN DEFAULT FALSE,
    is_invoiced BOOLEAN DEFAULT FALSE,
    status VARCHAR(50),
    price DECIMAL(19, 2),
    euro_price DECIMAL(19, 2),
    order_number VARCHAR(255) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;