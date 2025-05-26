-- Test data for the Belsign Photo Documentation System
-- This script inserts test data into all tables for testing purposes

-- Insert test users
INSERT INTO USERS (user_id, username, password, email, phone, name, nfc_id, status, created_at)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'admin', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS', 'admin@example.com', '1234567890', 'Admin User', 'NFC001', 'ACTIVE', datetime('now')),
    ('22222222-2222-2222-2222-222222222222', 'production', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS', 'production@example.com', '2345678901', 'Production Worker', 'NFC002', 'ACTIVE', datetime('now')),
    ('33333333-3333-3333-3333-333333333333', 'qa', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS', 'qa@example.com', '3456789012', 'Quality Assurance', 'NFC003', 'ACTIVE', datetime('now'));

-- Insert user roles
INSERT INTO USER_ROLES (user_id, role)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'ADMIN'),
    ('22222222-2222-2222-2222-222222222222', 'PRODUCTION'),
    ('33333333-3333-3333-3333-333333333333', 'QA');

-- Insert test customers
INSERT INTO CUSTOMERS (customer_id, name, email, phone, type, company_name, created_at)
VALUES
    ('44444444-4444-4444-4444-444444444444', 'Acme Corp', 'contact@acmecorp.com', '4567890123', 'BUSINESS', 'Acme Corporation', datetime('now')),
    ('55555555-5555-5555-5555-555555555555', 'TechSolutions', 'info@techsolutions.com', '5678901234', 'BUSINESS', 'Tech Solutions Inc.', datetime('now')),
    ('66666666-6666-6666-6666-666666666666', 'John Smith', 'john.smith@example.com', '6789012345', 'INDIVIDUAL', NULL, datetime('now'));

-- Insert test orders
INSERT INTO ORDERS (order_id, order_number, customer_id, created_by, created_at, modified_at, status, product_description, delivery_address, delivery_date)
VALUES
    ('77777777-7777-7777-7777-777777777777', '07/23-444444-12345001', '44444444-4444-4444-4444-444444444444', '11111111-1111-1111-1111-111111111111', datetime('now'), NULL, 'PENDING', 'Welding work on expansion joint', '123 Main St, Anytown, USA', date('now', '+30 days')),
    ('88888888-8888-8888-8888-888888888888', '07/23-555555-12345002', '55555555-5555-5555-5555-555555555555', '11111111-1111-1111-1111-111111111111', datetime('now'), NULL, 'IN_PROGRESS', 'Expansion joint assembly', '456 Tech Blvd, Innovation City, USA', date('now', '+45 days')),
    ('99999999-9999-9999-9999-999999999999', '07/23-666666-12345003', '66666666-6666-6666-6666-666666666666', '11111111-1111-1111-1111-111111111111', datetime('now'), NULL, 'COMPLETED', 'Pipe end-cap inspection', '789 Residential Ave, Hometown, USA', date('now', '+15 days'));

-- Update orders to assign them to production workers
UPDATE ORDERS SET assigned_to = '22222222-2222-2222-2222-222222222222' WHERE order_id = '77777777-7777-7777-7777-777777777777';
UPDATE ORDERS SET assigned_to = '22222222-2222-2222-2222-222222222222' WHERE order_id = '88888888-8888-8888-8888-888888888888';

-- Insert photo templates (if not already inserted by migrations)
INSERT OR IGNORE INTO PHOTO_TEMPLATES (template_id, name, description, created_at)
VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'TOP_VIEW_OF_JOINT', 'Take a photo from above the joint.', datetime('now')),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'SIDE_VIEW_OF_WELD', 'Take a photo from the side of the weld.', datetime('now')),
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'FRONT_VIEW_OF_ASSEMBLY', 'Take a photo from the front of the assembly.', datetime('now')),
    ('dddddddd-dddd-dddd-dddd-dddddddddddd', 'BACK_VIEW_OF_ASSEMBLY', 'Take a photo from the back of the assembly.', datetime('now'));

-- Associate orders with photo templates
INSERT INTO ORDER_PHOTO_TEMPLATES (order_id, template_id, required, created_at)
VALUES
    ('77777777-7777-7777-7777-777777777777', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 1, datetime('now')),
    ('77777777-7777-7777-7777-777777777777', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 1, datetime('now')),
    ('88888888-8888-8888-8888-888888888888', 'cccccccc-cccc-cccc-cccc-cccccccccccc', 1, datetime('now')),
    ('88888888-8888-8888-8888-888888888888', 'dddddddd-dddd-dddd-dddd-dddddddddddd', 1, datetime('now')),
    ('99999999-9999-9999-9999-999999999999', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 1, datetime('now')),
    ('99999999-9999-9999-9999-999999999999', 'cccccccc-cccc-cccc-cccc-cccccccccccc', 1, datetime('now'));

-- Insert test photos
INSERT INTO PHOTOS (photo_id, order_id, uploaded_by, uploaded_at, image_path, status, reviewed_by, reviewed_at, comments, template_id, modified_at)
VALUES
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', '77777777-7777-7777-7777-777777777777', '22222222-2222-2222-2222-222222222222', datetime('now', '-2 days'), 'src/test/resources/mock/camera/test_weld_top.jpg', 'PENDING', NULL, NULL, NULL, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', NULL),
    ('ffffffff-ffff-ffff-ffff-ffffffffffff', '77777777-7777-7777-7777-777777777777', '22222222-2222-2222-2222-222222222222', datetime('now', '-2 days'), 'src/test/resources/mock/camera/test_weld_side.jpg', 'APPROVED', '33333333-3333-3333-3333-333333333333', datetime('now', '-1 day'), 'Good quality image', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', datetime('now', '-1 day')),
    ('gggggggg-gggg-gggg-gggg-gggggggggggg', '88888888-8888-8888-8888-888888888888', '22222222-2222-2222-2222-222222222222', datetime('now', '-3 days'), 'src/test/resources/mock/camera/test_assembly_front.jpg', 'APPROVED', '33333333-3333-3333-3333-333333333333', datetime('now', '-2 days'), 'Clear image of assembly', 'cccccccc-cccc-cccc-cccc-cccccccccccc', datetime('now', '-2 days')),
    ('hhhhhhhh-hhhh-hhhh-hhhh-hhhhhhhhhhhh', '99999999-9999-9999-9999-999999999999', '22222222-2222-2222-2222-222222222222', datetime('now', '-5 days'), 'src/test/resources/mock/camera/test_joint_top.jpg', 'REJECTED', '33333333-3333-3333-3333-333333333333', datetime('now', '-4 days'), 'Image too blurry, please retake', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', datetime('now', '-4 days'));

-- Insert photo annotations
INSERT INTO PHOTO_ANNOTATIONS (annotation_id, photo_id, x_position, y_position, text, created_at)
VALUES
    ('iiiiiiii-iiii-iiii-iiii-iiiiiiiiiiii', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 100, 150, 'Check weld quality here', datetime('now', '-2 days')),
    ('jjjjjjjj-jjjj-jjjj-jjjj-jjjjjjjjjjjj', 'ffffffff-ffff-ffff-ffff-ffffffffffff', 200, 250, 'Good weld seam', datetime('now', '-1 day')),
    ('kkkkkkkk-kkkk-kkkk-kkkk-kkkkkkkkkkkk', 'gggggggg-gggg-gggg-gggg-gggggggggggg', 150, 200, 'Assembly alignment correct', datetime('now', '-2 days'));

-- Insert test reports
INSERT INTO REPORTS (report_id, order_id, generated_by, generated_at, recipient_id, format, status, comments, version, modified_at)
VALUES
    ('llllllll-llll-llll-llll-llllllllllll', '77777777-7777-7777-7777-777777777777', '33333333-3333-3333-3333-333333333333', datetime('now', '-1 day'), '44444444-4444-4444-4444-444444444444', 'PDF', 'PENDING', 'Initial quality report', 1, NULL),
    ('mmmmmmmm-mmmm-mmmm-mmmm-mmmmmmmmmmmm', '88888888-8888-8888-8888-888888888888', '33333333-3333-3333-3333-333333333333', datetime('now', '-2 days'), '55555555-5555-5555-5555-555555555555', 'PDF', 'APPROVED', 'Final quality report', 2, datetime('now', '-1 day')),
    ('nnnnnnnn-nnnn-nnnn-nnnn-nnnnnnnnnnnn', '99999999-9999-9999-9999-999999999999', '33333333-3333-3333-3333-333333333333', datetime('now', '-4 days'), '66666666-6666-6666-6666-666666666666', 'PDF', 'REJECTED', 'Photos need to be retaken', 1, datetime('now', '-3 days'));

-- Associate photos with reports
INSERT INTO REPORT_PHOTOS (report_id, photo_id)
VALUES
    ('llllllll-llll-llll-llll-llllllllllll', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee'),
    ('llllllll-llll-llll-llll-llllllllllll', 'ffffffff-ffff-ffff-ffff-ffffffffffff'),
    ('mmmmmmmm-mmmm-mmmm-mmmm-mmmmmmmmmmmm', 'gggggggg-gggg-gggg-gggg-gggggggggggg'),
    ('nnnnnnnn-nnnn-nnnn-nnnn-nnnnnnnnnnnn', 'hhhhhhhh-hhhh-hhhh-hhhh-hhhhhhhhhhhh');

-- Create directory for test images
-- Note: This is a comment only, as SQL cannot create directories.
-- You'll need to manually create the directory src/test/resources/mock/camera/
-- and add test images there, or copy them from the main resources.
