-- V9__Create_product_table.sql
CREATE TABLE product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    price DECIMAL(19, 2),
    euro_price DECIMAL(19, 2),
    coming_price DECIMAL(19, 2),
    available_quantity INT DEFAULT 0,
    is_deleted BOOLEAN DEFAULT FALSE,
    name VARCHAR(255),
    description TEXT,
    for_sale BOOLEAN DEFAULT FALSE,
    barcode VARCHAR(100),
    product_category VARCHAR(50),
    is_promoted BOOLEAN DEFAULT FALSE,
    percent INT DEFAULT 0,
    promotion_price DECIMAL(19, 2),
    promotion_euro_price DECIMAL(19, 2),
    image_data MEDIUMBLOB NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;