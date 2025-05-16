-- Drop indexes on PIN code and QR code hash
DROP INDEX idx_users_pin_code;
DROP INDEX idx_users_qr_code_hash;

-- Remove PIN code and QR code hash columns from users table
ALTER TABLE users
    DROP COLUMN pin_code;
ALTER TABLE users
    DROP COLUMN qr_code_hash;