USE BelSign;

-- Add test orders for the production user with ID 20b013bc-3366-4266-9043-48a96b00e8fd
-- This migration adds test orders and assigns them to the production user

-- First, check if the production user exists
IF NOT EXISTS (SELECT 1 FROM USERS WHERE user_id = '20b013bc-3366-4266-9043-48a96b00e8fd')
BEGIN
    INSERT INTO USERS (user_id, username, password, email, status, created_at)
    VALUES ('20b013bc-3366-4266-9043-48a96b00e8fd', 'production2', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS', 'production2@example.com', 'ACTIVE', SYSDATETIME());
END

-- Add PRODUCTION role for the user if it doesn't exist
IF NOT EXISTS (SELECT 1 FROM USER_ROLES WHERE user_id = '20b013bc-3366-4266-9043-48a96b00e8fd' AND role = 'PRODUCTION')
BEGIN
    INSERT INTO USER_ROLES (user_id, role)
    VALUES ('20b013bc-3366-4266-9043-48a96b00e8fd', 'PRODUCTION');
END

-- Add a test customer if it doesn't exist
IF NOT EXISTS (SELECT 1 FROM CUSTOMERS WHERE customer_id = 'aabbccdd-1122-3344-5566-778899aabbcc')
BEGIN
    INSERT INTO CUSTOMERS (customer_id, name, email, phone, type, company_name, created_at)
    VALUES ('aabbccdd-1122-3344-5566-778899aabbcc', 'Test Customer', 'test@example.com', '1234567890', 'BUSINESS', 'Test Company', SYSDATETIME());
END

-- Add test orders assigned to the production user
IF NOT EXISTS (SELECT 1 FROM ORDERS WHERE order_id = 'order001-aaaa-bbbb-cccc-ddddeeeeeeee')
BEGIN
    INSERT INTO ORDERS (order_id, order_number, customer_id, created_by, created_at, status, product_description, assigned_to)
    VALUES ('order001-aaaa-bbbb-cccc-ddddeeeeeeee', 'ORD-TEST-001', 'aabbccdd-1122-3344-5566-778899aabbcc', '20b013bc-3366-4266-9043-48a96b00e8fd', SYSDATETIME(), 'PENDING', 'Test order 1 for production user', '20b013bc-3366-4266-9043-48a96b00e8fd');
END

IF NOT EXISTS (SELECT 1 FROM ORDERS WHERE order_id = 'order002-ffff-gggg-hhhh-iiiijjjjjjjj')
BEGIN
    INSERT INTO ORDERS (order_id, order_number, customer_id, created_by, created_at, status, product_description, assigned_to)
    VALUES ('order002-ffff-gggg-hhhh-iiiijjjjjjjj', 'ORD-TEST-002', 'aabbccdd-1122-3344-5566-778899aabbcc', '20b013bc-3366-4266-9043-48a96b00e8fd', SYSDATETIME(), 'IN_PROGRESS', 'Test order 2 for production user', '20b013bc-3366-4266-9043-48a96b00e8fd');
END

-- Add photo templates if they don't exist
IF NOT EXISTS (SELECT 1 FROM PHOTO_TEMPLATES WHERE template_id = 'template1-1111-2222-3333-444455556666')
BEGIN
    INSERT INTO PHOTO_TEMPLATES (template_id, name, description, created_at)
    VALUES ('template1-1111-2222-3333-444455556666', 'TEST_TEMPLATE_1', 'Test template 1', SYSDATETIME());
END

IF NOT EXISTS (SELECT 1 FROM PHOTO_TEMPLATES WHERE template_id = 'template2-7777-8888-9999-000011112222')
BEGIN
    INSERT INTO PHOTO_TEMPLATES (template_id, name, description, created_at)
    VALUES ('template2-7777-8888-9999-000011112222', 'TEST_TEMPLATE_2', 'Test template 2', SYSDATETIME());
END

-- Associate orders with photo templates
IF NOT EXISTS (SELECT 1 FROM ORDER_PHOTO_TEMPLATES WHERE order_id = 'order001-aaaa-bbbb-cccc-ddddeeeeeeee' AND template_id = 'template1-1111-2222-3333-444455556666')
BEGIN
    INSERT INTO ORDER_PHOTO_TEMPLATES (order_id, template_id, required, created_at)
    VALUES ('order001-aaaa-bbbb-cccc-ddddeeeeeeee', 'template1-1111-2222-3333-444455556666', 1, SYSDATETIME());
END

IF NOT EXISTS (SELECT 1 FROM ORDER_PHOTO_TEMPLATES WHERE order_id = 'order002-ffff-gggg-hhhh-iiiijjjjjjjj' AND template_id = 'template2-7777-8888-9999-000011112222')
BEGIN
    INSERT INTO ORDER_PHOTO_TEMPLATES (order_id, template_id, required, created_at)
    VALUES ('order002-ffff-gggg-hhhh-iiiijjjjjjjj', 'template2-7777-8888-9999-000011112222', 1, SYSDATETIME());
END