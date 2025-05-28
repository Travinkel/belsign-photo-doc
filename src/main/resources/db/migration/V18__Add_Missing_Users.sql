-- Add Missing Users
-- This migration adds the missing users with the correct credentials

-- Update admin user to use bcrypt hashed password
UPDATE users
SET password = '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS'
WHERE username = 'admin';

-- Update qa_user to use bcrypt hashed password
UPDATE users
SET password = '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS'
WHERE username = 'qa_user';

-- Update production_worker password
UPDATE users
SET password = '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS'
WHERE username = 'production_worker';

-- Create production2 user if it doesn't exist
IF NOT EXISTS (SELECT 1 FROM users WHERE id = '20b013bc-3366-4266-9043-48a96b00e8fd')
BEGIN
    INSERT INTO users (id, username, password, email, status, first_name, last_name, created_at)
    VALUES ('20b013bc-3366-4266-9043-48a96b00e8fd', 'production2', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS', 'production2@example.com', 'ACTIVE', 'Production', 'User2', CURRENT_TIMESTAMP);
END

-- Add PRODUCTION role for production2 user if it doesn't exist
IF NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = '20b013bc-3366-4266-9043-48a96b00e8fd')
BEGIN
    INSERT INTO user_roles (user_id, role)
    VALUES ('20b013bc-3366-4266-9043-48a96b00e8fd', 'PRODUCTION');
END
