-- Update user IDs to UUID format

-- Create temporary tables
CREATE TABLE users_temp
(
    id           TEXT PRIMARY KEY,
    username     TEXT  NOT NULL UNIQUE,
    password     TEXT NOT NULL,
    first_name   TEXT,
    last_name    TEXT,
    email        TEXT NOT NULL UNIQUE,
    status       TEXT  NOT NULL,
    phone_number TEXT,
    nfc_id       TEXT,
    created_at   DATETIME2    DEFAULT (datetime('now')),
    updated_at   DATETIME2    DEFAULT (datetime('now'))
);

CREATE TABLE user_roles_temp
(
    user_id TEXT NOT NULL,
    role    TEXT NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users_temp (id) 
);

-- Insert data into temporary tables with UUID-based IDs
-- Admin user
INSERT INTO users_temp 
SELECT 
    hex(randomblob(16)), -- Generate a UUID
    username, 
    password, 
    first_name, 
    last_name, 
    email, 
    status, 
    phone_number, 
    nfc_id,
    created_at, 
    updated_at
FROM users 
WHERE id = 'admin-user-id';

-- Get the new UUID for the admin user
INSERT INTO user_roles_temp
SELECT 
    (SELECT id FROM users_temp WHERE email = (SELECT email FROM users WHERE id = 'admin-user-id')),
    role
FROM user_roles
WHERE user_id = 'admin-user-id';

-- Admin user 2
INSERT INTO users_temp 
SELECT 
    hex(randomblob(16)), -- Generate a UUID
    username, 
    password, 
    first_name, 
    last_name, 
    email, 
    status, 
    phone_number, 
    nfc_id,
    created_at, 
    updated_at
FROM users 
WHERE id = 'admin-user-id2';

-- Get the new UUID for the admin user 2
INSERT INTO user_roles_temp
SELECT 
    (SELECT id FROM users_temp WHERE email = (SELECT email FROM users WHERE id = 'admin-user-id2')),
    role
FROM user_roles
WHERE user_id = 'admin-user-id2';

-- QA user
INSERT INTO users_temp 
SELECT 
    hex(randomblob(16)), -- Generate a UUID
    username, 
    password, 
    first_name, 
    last_name, 
    email, 
    status, 
    phone_number, 
    nfc_id,
    created_at, 
    updated_at
FROM users 
WHERE id = 'qa-user-id';

-- Get the new UUID for the QA user
INSERT INTO user_roles_temp
SELECT 
    (SELECT id FROM users_temp WHERE email = (SELECT email FROM users WHERE id = 'qa-user-id')),
    role
FROM user_roles
WHERE user_id = 'qa-user-id';

-- Production user
INSERT INTO users_temp 
SELECT 
    hex(randomblob(16)), -- Generate a UUID
    username, 
    password, 
    first_name, 
    last_name, 
    email, 
    status, 
    phone_number, 
    nfc_id,
    created_at, 
    updated_at
FROM users 
WHERE id = 'production-user-id';

-- Get the new UUID for the production user
INSERT INTO user_roles_temp
SELECT 
    (SELECT id FROM users_temp WHERE email = (SELECT email FROM users WHERE id = 'production-user-id')),
    role
FROM user_roles
WHERE user_id = 'production-user-id';

-- Drop existing tables
DROP TABLE user_roles;
DROP TABLE users;

-- Rename temporary tables to original names
ALTER TABLE users_temp RENAME TO users;
ALTER TABLE user_roles_temp RENAME TO user_roles;

-- Create indexes
CREATE INDEX idx_users_username ON users (username);
CREATE INDEX idx_users_email ON users (email);