-- Fix for production user role
-- This migration ensures that the production user has the PRODUCTION role

-- First, make sure the production user exists
INSERT OR IGNORE INTO USERS (user_id, username, password, name, email, status, phone)
VALUES (hex(randomblob(16)), 'production', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS', 'Production User', 'production@belman.com', 'ACTIVE', '1234567890');

-- Then, add the PRODUCTION role to the production user if it doesn't already have it
INSERT OR IGNORE INTO USER_ROLES (user_id, role)
SELECT user_id, 'PRODUCTION'
FROM USERS
WHERE username = 'production' AND NOT EXISTS (
    SELECT 1 FROM USER_ROLES WHERE user_id = (SELECT user_id FROM USERS WHERE username = 'production') AND role = 'PRODUCTION'
);

-- Log the result
SELECT 'Production user role fixed: ' || (SELECT COUNT(*) FROM USER_ROLES WHERE user_id = (SELECT user_id FROM USERS WHERE username = 'production') AND role = 'PRODUCTION') || ' roles found';