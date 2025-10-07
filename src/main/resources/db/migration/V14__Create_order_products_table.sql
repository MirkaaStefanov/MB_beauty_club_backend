-- V14__Create_order_products_table.sql
CREATE TABLE order_products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quantity INT,
    product_id BIGINT,
    order_id BINARY(16),
    user_id BIGINT,
    is_deleted BOOLEAN DEFAULT FALSE,
    price DECIMAL(19, 2),
    euro_price DECIMAL(19, 2)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;