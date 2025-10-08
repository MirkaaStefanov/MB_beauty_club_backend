-- Flyway migration to create a shopping cart for the initial admin user.
-- This migration assumes that V16__Insert_Initial_Admin.sql has already run.

-- 1. Define the Admin's email (must match the previous migration)
SET @ADMIN_EMAIL = 'admin@mbbeautyclub.com';

-- 2. Retrieve the UUID (BINARY(16) ID) of the admin user
-- FIX: We explicitly cast the comparison string to prevent "Illegal mix of collations" error (Error Code: 1267).
SET @ADMIN_USER_ID = (
    SELECT id 
    FROM users 
    WHERE email = @ADMIN_EMAIL COLLATE utf8mb4_unicode_ci
    LIMIT 1
);

-- Check if the user ID was found before attempting to insert the cart
-- This conditional logic might vary depending on your specific MySQL version and setup
-- In standard MySQL, we rely on the subquery to return NULL if not found, but it's cleaner to be explicit:
-- If MySQL supports stored procedures or IF logic in migrations, it would be here.

-- 3. Insert the new shopping cart record
INSERT INTO shopping_carts (
    user_id,
    is_deleted
    -- Ако таблицата shopping_carts има колони за created_at/updated_at, добавете ги тук:
    -- created_at,
    -- updated_at
)
SELECT 
    @ADMIN_USER_ID,
    FALSE -- is_deleted
WHERE @ADMIN_USER_ID IS NOT NULL;
