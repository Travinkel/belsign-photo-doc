-- Update Production2 Password
-- This migration updates the password hash for the production2 user

-- Update the password hash for production2 user
UPDATE users
SET password = '$2a$10$wK0kEk40ULceNdJGSh1nNusm38CsKD1xBDcEyeLR/J4B2IONqRZUe'
WHERE username = 'production2';