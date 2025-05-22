-- Update password for the production user with ID 62ab92f5b9a1306b11791beb015d6d72
-- This fixes an issue where the password verification is failing

-- Update the password to match the expected password
UPDATE USERS
SET password = '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS'
WHERE user_id = '62ab92f5b9a1306b11791beb015d6d72'
AND EXISTS (SELECT 1 FROM USERS WHERE user_id = '62ab92f5b9a1306b11791beb015d6d72');