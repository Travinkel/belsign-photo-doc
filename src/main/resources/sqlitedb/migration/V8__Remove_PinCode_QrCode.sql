-- Drop indexes on PIN code and QR code hash
DROP INDEX idx_users_pin_code;
DROP INDEX idx_users_qr_code_hash;

-- SQLite doesn't support dropping columns directly, so we need to:
-- 1. Create a new table without the columns we want to drop
-- 2. Copy the data from the old table to the new table
-- 3. Drop the old table
-- 4. Rename the new table to the old table name

-- Create a new table without the pin_code and qr_code_hash columns
CREATE TABLE users_new
(
    id           TEXT PRIMARY KEY,
    username     TEXT  NOT NULL UNIQUE,
    password     TEXT NOT NULL,
    first_name   TEXT,
    last_name    TEXT,
    email        TEXT NOT NULL UNIQUE,
    status       TEXT  NOT NULL,
    phone_number TEXT,
    created_at   DATETIME2    DEFAULT (datetime('now')),
    updated_at   DATETIME2    DEFAULT (datetime('now')),
    nfc_id       TEXT
);

-- Copy data from the old table to the new table
INSERT INTO users_new (id, username, password, first_name, last_name, email, status, phone_number, created_at, updated_at, nfc_id)
SELECT id, username, password, first_name, last_name, email, status, phone_number, created_at, updated_at, nfc_id
FROM users;

-- Drop the old table
DROP TABLE users;

-- Rename the new table to the old table name
ALTER TABLE users_new RENAME TO users;

-- Recreate indexes
CREATE INDEX idx_users_username ON users (username);
CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_nfc_id ON users (nfc_id);