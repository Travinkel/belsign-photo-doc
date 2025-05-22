-- Add default admin user with UUID
INSERT INTO USERS (user_id, username, password, name, email, status, phone)
VALUES (hex(randomblob(16)), 'admin', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS', 'Admin User', 'admin@belman.com', 'ACTIVE', '1234567890');

-- Add admin role for the admin user
INSERT INTO USER_ROLES (user_id, role)
VALUES ((SELECT user_id FROM USERS WHERE email = 'admin@belman.com'), 'ADMIN');

-- Add another admin user with different password (for Android compatibility)
INSERT INTO USERS (user_id, username, password, name, email, status, phone)
VALUES (hex(randomblob(16)), 'admin2', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS', 'Admin User', 'admin2@belman.com', 'ACTIVE', '1234567890');

-- Add admin role for the second admin user
INSERT INTO USER_ROLES (user_id, role)
VALUES ((SELECT user_id FROM USERS WHERE email = 'admin2@belman.com'), 'ADMIN');

-- Add QA user
INSERT INTO USERS (user_id, username, password, name, email, status, phone)
VALUES (hex(randomblob(16)), 'qa_user', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS', 'QA User', 'qa_user@belman.com', 'ACTIVE', '1234567890');

-- Add QA role for the QA user
INSERT INTO USER_ROLES (user_id, role)
VALUES ((SELECT user_id FROM USERS WHERE email = 'qa_user@belman.com'), 'QA');

-- Add production user
INSERT INTO USERS (user_id, username, password, name, email, status, phone)
VALUES (hex(randomblob(16)), 'production', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS', 'Production User', 'production@belman.com', 'ACTIVE', '1234567890');

-- Add production role for the production user
INSERT INTO USER_ROLES (user_id, role)
VALUES ((SELECT user_id FROM USERS WHERE email = 'production@belman.com'), 'PRODUCTION');
