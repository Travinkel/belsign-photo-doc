-- Enable XACT_ABORT to ensure any error causes the entire transaction to be rolled back
SET XACT_ABORT ON;

-- Delete any existing conflicting data first
DELETE FROM PHOTOS;
DELETE FROM ORDERS;
DELETE FROM USER_ROLES;
DELETE FROM USERS;
DELETE FROM CUSTOMERS;

-- Default users with valid UUIDs
INSERT INTO USERS (user_id, username, password, email, status, name, nfc_id, created_at)
VALUES ('171c5cf9-5c35-4b6d-86d9-af16836f5e54', 'admin', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS', 'admin@belsign.com', 'ACTIVE', 'Admin User', 'NFC-ADMIN-001',
        CURRENT_TIMESTAMP),
       ('2c4c9f5b-4e0b-4e91-94a1-fb2d0532e637', 'qa_user', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS', 'qa@belsign.com', 'ACTIVE', 'QA User', 'NFC-QA-001',
        CURRENT_TIMESTAMP),
       ('c0d872d3-0c7f-4e17-9773-1f4e9647b964', 'production', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS', 'production@belsign.com', 'ACTIVE',
        'Production User', 'NFC-PROD-001', CURRENT_TIMESTAMP),
       ('20b013bc-3366-4266-9043-48a96b00e8fd', 'production2', '$2a$10$wK0kEk40ULceNdJGSh1nNusm38CsKD1xBDcEyeLR/J4B2IONqRZUe', 'production2@example.com', 'ACTIVE',
        'Production User2', 'NFC-PROD-002', CURRENT_TIMESTAMP),
       ('3e5f7a8b-9c2d-4e6f-8a1b-2c3d4e5f6a7b', 'production_worker', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS', 'production_worker@example.com', 'ACTIVE',
        'Production Worker', 'NFC-PROD-003', CURRENT_TIMESTAMP);

-- User roles
INSERT INTO USER_ROLES (user_id, role)
VALUES ('171c5cf9-5c35-4b6d-86d9-af16836f5e54', 'ADMIN'),
       ('2c4c9f5b-4e0b-4e91-94a1-fb2d0532e637', 'QA'),
       ('c0d872d3-0c7f-4e17-9773-1f4e9647b964', 'PRODUCTION'),
       ('20b013bc-3366-4266-9043-48a96b00e8fd', 'PRODUCTION'),
       ('3e5f7a8b-9c2d-4e6f-8a1b-2c3d4e5f6a7b', 'PRODUCTION');

-- Customers
INSERT INTO CUSTOMERS (customer_id, name, email, phone, type, company_name, created_at)
VALUES ('1', 'Lars Jensen', 'contact@danish-energy.dk', '+45 12345678', 'COMPANY', 'Danish Energy Company', CURRENT_TIMESTAMP),
       ('2', 'Hans Schmidt', 'info@german-manufacturing.de', '+49 9876543210', 'COMPANY', 'German Manufacturing GmbH', CURRENT_TIMESTAMP),
       ('3', 'Pierre Dupont', 'contact@french-industrial.fr', '+33 123456789', 'COMPANY', 'French Industrial Solutions', CURRENT_TIMESTAMP);

-- Orders
INSERT INTO ORDERS (order_id, order_number, customer_id, created_by, status, created_at)
VALUES ('1', 'ORD-45-230501-WLD-0001', '1', 'c0d872d3-0c7f-4e17-9773-1f4e9647b964', 'APPROVED', '2023-05-01 10:00:00'),
       ('2', 'ORD-49-230615-EXP-0002', '2', 'c0d872d3-0c7f-4e17-9773-1f4e9647b964', 'APPROVED', '2023-06-15 09:00:00'),
       ('3', 'ORD-33-230720-PIP-0003', '3', 'c0d872d3-0c7f-4e17-9773-1f4e9647b964', 'APPROVED', '2023-07-20 14:00:00');

-- Photos
INSERT INTO PHOTOS (photo_id, order_id, image_path, uploaded_by, uploaded_at, status, reviewed_by, reviewed_at, comments)
VALUES ('1', '1', '/photos/2023/05/01/ORD-45-230501-WLD-0001/img1.jpg', 'c0d872d3-0c7f-4e17-9773-1f4e9647b964', '2023-05-01 10:15:30', 'APPROVED', '2c4c9f5b-4e0b-4e91-94a1-fb2d0532e637', '2023-05-01 11:30:00', NULL),
       ('2', '1', '/photos/2023/05/01/ORD-45-230501-WLD-0001/img2.jpg', 'c0d872d3-0c7f-4e17-9773-1f4e9647b964', '2023-05-01 10:16:45', 'APPROVED', '2c4c9f5b-4e0b-4e91-94a1-fb2d0532e637', '2023-05-01 11:31:00', NULL),
       ('3', '1', '/photos/2023/05/01/ORD-45-230501-WLD-0001/img3.jpg', 'c0d872d3-0c7f-4e17-9773-1f4e9647b964', '2023-05-01 10:18:20', 'APPROVED', '2c4c9f5b-4e0b-4e91-94a1-fb2d0532e637', '2023-05-01 11:32:00', NULL),
       ('4', '2', '/photos/2023/06/15/ORD-49-230615-EXP-0002/img1.jpg', 'c0d872d3-0c7f-4e17-9773-1f4e9647b964', '2023-06-15 09:30:15', 'APPROVED', '2c4c9f5b-4e0b-4e91-94a1-fb2d0532e637', '2023-06-15 10:45:00', NULL),
       ('5', '2', '/photos/2023/06/15/ORD-49-230615-EXP-0002/img2.jpg', 'c0d872d3-0c7f-4e17-9773-1f4e9647b964', '2023-06-15 09:32:40', 'APPROVED', '2c4c9f5b-4e0b-4e91-94a1-fb2d0532e637', '2023-06-15 10:46:00', NULL),
       ('6', '2', '/photos/2023/06/15/ORD-49-230615-EXP-0002/img3.jpg', 'c0d872d3-0c7f-4e17-9773-1f4e9647b964', '2023-06-15 09:35:10', 'APPROVED', '2c4c9f5b-4e0b-4e91-94a1-fb2d0532e637', '2023-06-15 10:47:00', NULL),
       ('7', '2', '/photos/2023/06/15/ORD-49-230615-EXP-0002/img4.jpg', 'c0d872d3-0c7f-4e17-9773-1f4e9647b964', '2023-06-15 09:38:25', 'APPROVED', '2c4c9f5b-4e0b-4e91-94a1-fb2d0532e637', '2023-06-15 10:48:00', NULL),
       ('8', '3', '/photos/2023/07/20/ORD-33-230720-PIP-0003/img1.jpg', 'c0d872d3-0c7f-4e17-9773-1f4e9647b964', '2023-07-20 14:20:10', 'APPROVED', '2c4c9f5b-4e0b-4e91-94a1-fb2d0532e637', '2023-07-20 15:30:00', NULL),
       ('9', '3', '/photos/2023/07/20/ORD-33-230720-PIP-0003/img2.jpg', 'c0d872d3-0c7f-4e17-9773-1f4e9647b964', '2023-07-20 14:22:35', 'APPROVED', '2c4c9f5b-4e0b-4e91-94a1-fb2d0532e637', '2023-07-20 15:31:00', NULL),
       ('10', '3', '/photos/2023/07/20/ORD-33-230720-PIP-0003/img3.jpg', 'c0d872d3-0c7f-4e17-9773-1f4e9647b964', '2023-07-20 14:25:50', 'REJECTED', '2c4c9f5b-4e0b-4e91-94a1-fb2d0532e637', '2023-07-20 15:32:00', 'Pressure gauge not visible in image'),
       ('11', '3', '/photos/2023/07/20/ORD-33-230720-PIP-0003/img4.jpg', 'c0d872d3-0c7f-4e17-9773-1f4e9647b964', '2023-07-20 14:30:15', 'APPROVED', '2c4c9f5b-4e0b-4e91-94a1-fb2d0532e637', '2023-07-20 15:33:00', NULL),
       ('12', '3', '/photos/2023/07/20/ORD-33-230720-PIP-0003/img5.jpg', 'c0d872d3-0c7f-4e17-9773-1f4e9647b964', '2023-07-20 14:35:40', 'APPROVED', '2c4c9f5b-4e0b-4e91-94a1-fb2d0532e637', '2023-07-20 15:34:00', NULL);

