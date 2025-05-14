-- Create Users Table
CREATE TABLE users
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
    updated_at   DATETIME2    DEFAULT (datetime('now'))
);

-- Create User Roles Table
CREATE TABLE user_roles
(
    user_id TEXT NOT NULL,
    role    TEXT NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users (id) 
);

-- Create Index on Username
CREATE INDEX idx_users_username ON users (username);

-- Create Index on Email
CREATE INDEX idx_users_email ON users (email);