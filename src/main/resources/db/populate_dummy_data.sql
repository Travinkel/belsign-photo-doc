-- Delete any existing conflicting data first
DELETE FROM photo_documents;
DELETE FROM orders;
DELETE FROM user_roles;
DELETE FROM users;
DELETE FROM customers;

-- Default users with valid UUIDs
INSERT INTO users (id, username, password, email, status, first_name, last_name, created_at)
VALUES
    ('171c5cf9-5c35-4b6d-86d9-af16836f5e54', 'admin', 'password123', 'admin@belsign.com', 'ACTIVE', 'Admin', 'User', CURRENT_TIMESTAMP),
    ('2c4c9f5b-4e0b-4e91-94a1-fb2d0532e637', 'qa_user', 'password123', 'qa@belsign.com', 'ACTIVE', 'QA', 'User', CURRENT_TIMESTAMP),
    ('c0d872d3-0c7f-4e17-9773-1f4e9647b964', 'production_worker', 'password123', 'production@belsign.com', 'ACTIVE', 'Production', 'Worker', CURRENT_TIMESTAMP);

-- User roles
INSERT INTO user_roles (user_id, role)
VALUES
    ('171c5cf9-5c35-4b6d-86d9-af16836f5e54', 'ADMIN'),
    ('2c4c9f5b-4e0b-4e91-94a1-fb2d0532e637', 'QA'),
    ('c0d872d3-0c7f-4e17-9773-1f4e9647b964', 'PRODUCTION');

-- Customers
INSERT INTO customers (id, type, company_name, first_name, last_name, email, phone_number)
VALUES
    ('1', 'COMPANY', 'Danish Energy Company', 'Lars', 'Jensen', 'contact@danish-energy.dk', '+45 12345678'),
    ('2', 'COMPANY', 'German Manufacturing GmbH', 'Hans', 'Schmidt', 'info@german-manufacturing.de', '+49 9876543210'),
    ('3', 'COMPANY', 'French Industrial Solutions', 'Pierre', 'Dupont', 'contact@french-industrial.fr', '+33 123456789');

-- Orders (created_by matches production_worker UUID)
INSERT INTO orders (id, order_number, customer_id, status, created_by, created_at)
VALUES
    ('1', 'ORD-45-230501-WLD-0001', '1', 'APPROVED', 'c0d872d3-0c7f-4e17-9773-1f4e9647b964', '2023-05-01 10:00:00'),
    ('2', 'ORD-49-230615-EXP-0002', '2', 'APPROVED', 'c0d872d3-0c7f-4e17-9773-1f4e9647b964', '2023-06-15 09:00:00'),
    ('3', 'ORD-33-230720-PIP-0003', '3', 'APPROVED', 'c0d872d3-0c7f-4e17-9773-1f4e9647b964', '2023-07-20 14:00:00');

-- Photo documents
INSERT INTO photo_documents (id, order_id, image_path, angle, status, uploaded_by, uploaded_at, reviewed_by, reviewed_at, review_comment)
VALUES
    ('1', '1', '/photos/2023/05/01/ORD-45-230501-WLD-0001/img1.jpg', 'FRONT', 'APPROVED', 'c0d872d3-0c7f-4e17-9773-1f4e9647b964', '2023-05-01 10:15:30', '2c4c9f5b-4e0b-4e91-94a1-fb2d0532e637', '2023-05-01 11:30:00', NULL),
    ('2', '1', '/photos/2023/05/01/ORD-45-230501-WLD-0001/img2.jpg', 'SIDE', 'APPROVED', 'c0d872d3-0c7f-4e17-9773-1f4e9647b964', '2023-05-01 10:16:45', '2c4c9f5b-4e0b-4e91-94a1-fb2d0532e637', '2023-05-01 11:31:00', NULL),
    ('3', '1', '/photos/2023/05/01/ORD-45-230501-WLD-0001/img3.jpg', 'DETAIL', 'APPROVED', 'c0d872d3-0c7f-4e17-9773-1f4e9647b964', '2023-05-01 10:18:20', '2c4c9f5b-4e0b-4e91-94a1-fb2d0532e637', '2023-05-01 11:32:00', NULL),

    ('4', '2', '/photos/2023/06/15/ORD-49-230615-EXP-0002/img1.jpg', 'FRONT', 'APPROVED', 'c0d872d3-0c7f-4e17-9773-1f4e9647b964', '2023-06-15 09:30:15', '2c4c9f5b-4e0b-4e91-94a1-fb2d0532e637', '2023-06-15 10:45:00', NULL),
    ('5', '2', '/photos/2023/06/15/ORD-49-230615-EXP-0002/img2.jpg', 'SIDE', 'APPROVED', 'c0d872d3-0c7f-4e17-9773-1f4e9647b964', '2023-06-15 09:32:40', '2c4c9f5b-4e0b-4e91-94a1-fb2d0532e637', '2023-06-15 10:46:00', NULL),
    ('6', '2', '/photos/2023/06/15/ORD-49-230615-EXP-0002/img3.jpg', 'DETAIL', 'APPROVED', 'c0d872d3-0c7f-4e17-9773-1f4e9647b964', '2023-06-15 09:35:10', '2c4c9f5b-4e0b-4e91-94a1-fb2d0532e637', '2023-06-15 10:47:00', NULL),
    ('7', '2', '/photos/2023/06/15/ORD-49-230615-EXP-0002/img4.jpg', 'DETAIL', 'APPROVED', 'c0d872d3-0c7f-4e17-9773-1f4e9647b964', '2023-06-15 09:38:25', '2c4c9f5b-4e0b-4e91-94a1-fb2d0532e637', '2023-06-15 10:48:00', NULL),

    ('8', '3', '/photos/2023/07/20/ORD-33-230720-PIP-0003/img1.jpg', 'FRONT', 'APPROVED', 'c0d872d3-0c7f-4e17-9773-1f4e9647b964', '2023-07-20 14:20:10', '2c4c9f5b-4e0b-4e91-94a1-fb2d0532e637', '2023-07-20 15:30:00', NULL),
    ('9', '3', '/photos/2023/07/20/ORD-33-230720-PIP-0003/img2.jpg', 'SIDE', 'APPROVED', 'c0d872d3-0c7f-4e17-9773-1f4e9647b964', '2023-07-20 14:22:35', '2c4c9f5b-4e0b-4e91-94a1-fb2d0532e637', '2023-07-20 15:31:00', NULL),
    ('10', '3', '/photos/2023/07/20/ORD-33-230720-PIP-0003/img3.jpg', 'DETAIL', 'REJECTED', 'c0d872d3-0c7f-4e17-9773-1f4e9647b964', '2023-07-20 14:25:50', '2c4c9f5b-4e0b-4e91-94a1-fb2d0532e637', '2023-07-20 15:32:00', 'Pressure gauge not visible in image'),
    ('11', '3', '/photos/2023/07/20/ORD-33-230720-PIP-0003/img4.jpg', 'DETAIL', 'APPROVED', 'c0d872d3-0c7f-4e17-9773-1f4e9647b964', '2023-07-20 14:30:15', '2c4c9f5b-4e0b-4e91-94a1-fb2d0532e637', '2023-07-20 15:33:00', NULL),
    ('12', '3', '/photos/2023/07/20/ORD-33-230720-PIP-0003/img5.jpg', 'DETAIL', 'APPROVED', 'c0d872d3-0c7f-4e17-9773-1f4e9647b964', '2023-07-20 14:35:40', '2c4c9f5b-4e0b-4e91-94a1-fb2d0532e637', '2023-07-20 15:34:00', NULL);
