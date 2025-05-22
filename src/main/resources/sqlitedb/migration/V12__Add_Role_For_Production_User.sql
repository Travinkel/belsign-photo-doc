-- Add PRODUCTION role for the production user with ID 62ab92f5b9a1306b11791beb015d6d72
-- This fixes an issue where the user exists but has no roles assigned

-- First, check if the user exists
INSERT INTO USER_ROLES (user_id, role)
SELECT '62ab92f5b9a1306b11791beb015d6d72', 'PRODUCTION'
WHERE EXISTS (SELECT 1 FROM USERS WHERE user_id = '62ab92f5b9a1306b11791beb015d6d72')
AND NOT EXISTS (SELECT 1 FROM USER_ROLES WHERE user_id = '62ab92f5b9a1306b11791beb015d6d72' AND role = 'PRODUCTION');