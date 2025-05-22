-- R__seed_dev_data.sql
-- This is a repeatable migration that seeds development data into the database.
-- It will only run in development mode and will be skipped in production mode.

-- Only proceed if we're in development mode
-- This check is handled by the Flyway callback in SqliteDatabaseConfig

-- Seed test users if they don't exist
INSERT OR IGNORE INTO USERS (user_id, username, password, email, name, status, created_at)
VALUES 
    ('admin-dev-1', 'admin', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS', 'admin@example.com', 'Admin User', 'ACTIVE', datetime('now')),
    ('prod-dev-1', 'production', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS', 'production@example.com', 'Production User', 'ACTIVE', datetime('now')),
    ('qa-dev-1', 'qa', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS', 'qa@example.com', 'QA User', 'ACTIVE', datetime('now'));

-- Seed user roles if they don't exist
INSERT OR IGNORE INTO USER_ROLES (user_id, role)
VALUES 
    ('admin-dev-1', 'ADMIN'),
    ('prod-dev-1', 'PRODUCTION'),
    ('qa-dev-1', 'QA');

-- Seed test customers if they don't exist
INSERT OR IGNORE INTO CUSTOMERS (customer_id, name, email, type, company_name, created_at)
VALUES 
    ('cust-dev-1', 'Test Customer 1', 'customer1@example.com', 'BUSINESS', 'Test Company 1', datetime('now')),
    ('cust-dev-2', 'Test Customer 2', 'customer2@example.com', 'BUSINESS', 'Test Company 2', datetime('now')),
    ('cust-dev-3', 'Test Customer 3', 'customer3@example.com', 'BUSINESS', 'Test Company 3', datetime('now'));

-- Seed test orders if they don't exist
INSERT OR IGNORE INTO ORDERS (order_id, order_number, customer_id, created_by, status, product_description, delivery_address, delivery_date, created_at, assigned_to)
VALUES 
    ('order-dev-1', 'ORD-123-DEV-0001', 'cust-dev-1', 'admin-dev-1', 'PENDING', 'Test Product 1', '123 Test St, Test City', date('now', '+14 days'), datetime('now'), 'prod-dev-1'),
    ('order-dev-2', 'ORD-456-DEV-0002', 'cust-dev-2', 'admin-dev-1', 'PENDING', 'Test Product 2', '456 Test St, Test City', date('now', '+21 days'), datetime('now'), 'prod-dev-1'),
    ('order-dev-3', 'ORD-789-DEV-0003', 'cust-dev-3', 'admin-dev-1', 'PENDING', 'Test Product 3', '789 Test St, Test City', date('now', '+28 days'), datetime('now'), NULL);

-- Get template IDs
WITH templates AS (
    SELECT template_id FROM PHOTO_TEMPLATES
)
-- Associate templates with orders
INSERT OR IGNORE INTO ORDER_PHOTO_TEMPLATES (order_id, template_id, required)
SELECT 'order-dev-1', template_id, 1 FROM templates
UNION ALL
SELECT 'order-dev-2', template_id, 1 FROM templates
UNION ALL
SELECT 'order-dev-3', template_id, 1 FROM templates;

-- Log that the dev data has been seeded
-- This is just a comment for documentation purposes, as SQLite doesn't support PRINT or similar commands
-- The application will log this information when the migration is run