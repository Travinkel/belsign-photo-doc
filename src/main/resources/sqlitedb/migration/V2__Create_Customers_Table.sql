-- Create Customers Table
CREATE TABLE customers
(
    id           TEXT PRIMARY KEY,
    type         TEXT  NOT NULL,
    first_name   TEXT,
    last_name    TEXT,
    company_name TEXT,
    email        TEXT NOT NULL,
    phone_number TEXT,
    created_at   TEXT    NOT NULL DEFAULT (datetime('now')),
    updated_at   TEXT    NOT NULL DEFAULT (datetime('now'))
);

-- Create Index on Email
CREATE INDEX idx_customers_email ON customers (email);

-- Create Index on Type
CREATE INDEX idx_customers_type ON customers (type);