-- Add NFC ID column to users table if it doesn't exist
-- Using a simpler approach that's more compatible with Flyway's SQL parser

-- Attempt to add the column directly
-- This will fail silently if the column already exists (due to error handling in SqliteDatabaseConfig)
ALTER TABLE users ADD COLUMN nfc_id TEXT;

-- Create index on NFC ID for faster lookups if it doesn't exist
CREATE INDEX IF NOT EXISTS idx_users_nfc_id ON users (nfc_id);

-- Update existing users with default NFC ID (for testing)
UPDATE users
SET nfc_id = 'nfc123456'
WHERE username = 'production';
