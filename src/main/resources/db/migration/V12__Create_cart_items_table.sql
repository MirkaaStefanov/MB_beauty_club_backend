-- V12__Create_cart_items_table.sql
CREATE TABLE cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT,
    shopping_cart_id BIGINT,
    quantity INT,
    price DECIMAL(19, 2),
    euro_price DECIMAL(19, 2),
    is_deleted BOOLEAN DEFAULT FALSE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;