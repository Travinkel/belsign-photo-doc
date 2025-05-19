-- Add default admin user
INSERT INTO users (id, username, password, first_name, last_name, email, status, phone_number)
VALUES ('admin-user-id', 'admin', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS', 'Admin', 'User', 'admin@belman.com', 'ACTIVE', '1234567890');

-- Add admin role for the admin user
INSERT INTO user_roles (user_id, role)
VALUES ('admin-user-id', 'ADMIN');

-- Add another admin user with different password (for Android compatibility)
INSERT INTO users (id, username, password, first_name, last_name, email, status, phone_number)
VALUES ('admin-user-id2', 'admin2', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS', 'Admin', 'User', 'admin2@belman.com', 'ACTIVE', '1234567890');

-- Add admin role for the second admin user
INSERT INTO user_roles (user_id, role)
VALUES ('admin-user-id2', 'ADMIN');

-- Add QA user
INSERT INTO users (id, username, password, first_name, last_name, email, status, phone_number)
VALUES ('qa-user-id', 'qa_user', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS', 'QA', 'User', 'qa_user@belman.com', 'ACTIVE', '1234567890');

-- Add QA role for the QA user
INSERT INTO user_roles (user_id, role)
VALUES ('qa-user-id', 'QA');

-- Add production user
INSERT INTO users (id, username, password, first_name, last_name, email, status, phone_number)
VALUES ('production-user-id', 'production', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS', 'Production', 'User', 'production@belman.com', 'ACTIVE', '1234567890');

-- Add production role for the production user
INSERT INTO user_roles (user_id, role)
VALUES ('production-user-id', 'PRODUCTION');