-- Add PIN code and QR code columns to users table
ALTER TABLE users
    ADD COLUMN pin_code VARCHAR(10);
ALTER TABLE users
    ADD COLUMN qr_code_hash VARCHAR(100);

-- Create index on PIN code for faster lookups
CREATE INDEX idx_users_pin_code ON users (pin_code);

-- Create index on QR code hash for faster lookups
CREATE INDEX idx_users_qr_code_hash ON users (qr_code_hash);

-- Update existing users with default PIN code (for testing)
UPDATE users
SET pin_code = '1234'
WHERE username = 'pin_user';

-- Update existing users with default QR code hash (for testing)
UPDATE users
SET qr_code_hash = 'scanner123hash'
WHERE username = 'scanner';