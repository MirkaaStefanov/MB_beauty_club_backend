SET @ADMIN_EMAIL = 'admin@mbbeautyclub.com';
SET @ADMIN_PASSWORD_HASH = '$2a$12$DpkhWYmyMmoDPTwm1qGmRuZW5zzmHRDqzkIqGOBCdGqJ0uNCtMG3W';

-- 2. Insert the user record
INSERT INTO users (
    id,
    name,
    surname,
    email,
    password,
    role,
    provider,
    enabled,
    created_at,
    updated_at
)
VALUES (
    -- Generate a new UUID and convert it to BINARY(16) for MySQL
    UNHEX(REPLACE(UUID(), '-', '')),
    'Super', -- Name
    'Admin', -- Surname
    @ADMIN_EMAIL,
    @ADMIN_PASSWORD_HASH,
    'ADMIN', -- Role (must match com.example.MB_beauty_club_backend.enums.Role)
    'LOCAL', -- Provider (must match com.example.MB_beauty_club_backend.enums.Provider)
    TRUE,    -- Enabled
    NOW(),   -- created_at
    NOW()    -- updated_at
);