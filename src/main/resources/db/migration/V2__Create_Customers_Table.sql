USE
BelSign;

-- Create Customers Table
CREATE TABLE customers
(
    id           VARCHAR(36) PRIMARY KEY,
    type         VARCHAR(20)  NOT NULL,
    first_name   VARCHAR(50),
    last_name    VARCHAR(50),
    company_name VARCHAR(100),
    email        VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    created_at   DATETIME2    NOT NULL DEFAULT SYSDATETIME(),
    updated_at   DATETIME2    NOT NULL DEFAULT SYSDATETIME()
);

-- Create Index on Email
CREATE INDEX idx_customers_email ON customers (email);

-- Create Index on Type
CREATE INDEX idx_customers_type ON customers (type);