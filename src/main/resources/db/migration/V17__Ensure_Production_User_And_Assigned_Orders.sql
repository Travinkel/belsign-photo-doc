-- Ensure Production User and Assigned Orders
-- This migration ensures that a production user exists and orders are assigned to it

-- Create a production user if it doesn't exist
INSERT OR IGNORE INTO USERS (user_id, username, password, email, name, status, created_at)
VALUES ('production-user-id', 'production', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS', 'production@example.com', 'Production User', 'ACTIVE', datetime('now'));

-- Add PRODUCTION role for the user if it doesn't exist
INSERT OR IGNORE INTO USER_ROLES (user_id, role)
VALUES ('production-user-id', 'PRODUCTION');

-- Create production2 user if it doesn't exist
INSERT OR IGNORE INTO USERS (user_id, username, password, email, name, status, created_at)
VALUES ('20b013bc-3366-4266-9043-48a96b00e8fd', 'production2', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS', 'production2@example.com', 'Production User 2', 'ACTIVE', datetime('now'));

-- Add PRODUCTION role for production2 user if it doesn't exist
INSERT OR IGNORE INTO USER_ROLES (user_id, role)
VALUES ('20b013bc-3366-4266-9043-48a96b00e8fd', 'PRODUCTION');

-- Create a test customer if none exist
INSERT OR IGNORE INTO CUSTOMERS (customer_id, name, email, phone, type, company_name, created_at)
VALUES ('aabbccdd-1122-3344-5566-778899aabbcc', 'Test Customer', 'test@example.com', '1234567890', 'BUSINESS', 'Test Company', datetime('now'));

-- Create test orders assigned to the production user if none exist
INSERT OR IGNORE INTO ORDERS (order_id, order_number, customer_id, created_by, created_at, modified_at, status, product_description, assigned_to)
VALUES ('order-test-001', 'ORD-01-230101-ABC-0001', 'aabbccdd-1122-3344-5566-778899aabbcc', 'production-user-id', datetime('now'), datetime('now'), 'PENDING', 'Test order 1', 'production-user-id');

INSERT OR IGNORE INTO ORDERS (order_id, order_number, customer_id, created_by, created_at, modified_at, status, product_description, assigned_to)
VALUES ('order-test-002', 'ORD-02-230101-ABC-0002', 'aabbccdd-1122-3344-5566-778899aabbcc', 'production-user-id', datetime('now'), datetime('now'), 'IN_PROGRESS', 'Test order 2', 'production-user-id');

-- Ensure all orders are assigned to a production user
UPDATE ORDERS
SET assigned_to = 'production-user-id'
WHERE assigned_to IS NULL;
