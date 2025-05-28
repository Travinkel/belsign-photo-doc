-- Add Production Worker User
-- This migration adds the production_worker user with the correct credentials

-- Create production_worker user if it doesn't exist
IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'production_worker')
BEGIN
    INSERT INTO users (id, username, password, email, status, first_name, last_name, created_at)
    VALUES (NEWID(), 'production_worker', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS', 'production_worker@example.com', 'ACTIVE', 'Production', 'Worker', CURRENT_TIMESTAMP);
END

-- Add PRODUCTION role for production_worker user if it doesn't exist
IF EXISTS (SELECT 1 FROM users WHERE username = 'production_worker')
   AND NOT EXISTS (SELECT 1 FROM user_roles ur JOIN users u ON ur.user_id = u.id WHERE u.username = 'production_worker')
BEGIN
    INSERT INTO user_roles (user_id, role)
    SELECT id, 'PRODUCTION'
    FROM users
    WHERE username = 'production_worker';
END
