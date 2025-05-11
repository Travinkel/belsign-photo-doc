USE
BelSign;


-- Create Users Table
CREATE TABLE users
(
    id           VARCHAR(36) PRIMARY KEY,
    username     VARCHAR(50)  NOT NULL UNIQUE,
    password     VARCHAR(100) NOT NULL,
    first_name   VARCHAR(50),
    last_name    VARCHAR(50),
    email        VARCHAR(100) NOT NULL UNIQUE,
    status       VARCHAR(20)  NOT NULL,
    phone_number VARCHAR(20),
    created_at   DATETIME2    NOT NULL DEFAULT SYSDATETIME(),
    updated_at   DATETIME2    NOT NULL DEFAULT SYSDATETIME()
);

-- Create User Roles Table
CREATE TABLE user_roles
(
    user_id VARCHAR(36) NOT NULL,
    role    VARCHAR(20) NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Create Index on Username
CREATE INDEX idx_users_username ON users (username);

-- Create Index on Email
CREATE INDEX idx_users_email ON users (email);
