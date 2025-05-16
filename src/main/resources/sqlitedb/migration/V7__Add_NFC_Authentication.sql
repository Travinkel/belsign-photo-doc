-- Add NFC ID column to users table
ALTER TABLE users
    ADD COLUMN nfc_id TEXT;

-- Create index on NFC ID for faster lookups
CREATE INDEX idx_users_nfc_id ON users (nfc_id);

-- Update existing users with default NFC ID (for testing)
UPDATE users
SET nfc_id = 'nfc123456'
WHERE username = 'production';