USE BelSign;

-- Drop existing tables that will be replaced
IF OBJECT_ID('photo_documents', 'U') IS NOT NULL DROP TABLE photo_documents;
IF OBJECT_ID('orderAggregates', 'U') IS NOT NULL DROP TABLE orderAggregates;
IF OBJECT_ID('customers', 'U') IS NOT NULL DROP TABLE customers;
IF OBJECT_ID('user_roles', 'U') IS NOT NULL DROP TABLE user_roles;
IF OBJECT_ID('users', 'U') IS NOT NULL DROP TABLE users;

-- Create USERS Table
CREATE TABLE USERS (
    user_id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    name VARCHAR(100),
    nfc_id VARCHAR(50),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    modified_at DATETIME2,
    CONSTRAINT uk_username UNIQUE (username),
    CONSTRAINT uk_email UNIQUE (email),
    CONSTRAINT uk_nfc_id UNIQUE (nfc_id)
);

-- Create USER_ROLES Table
CREATE TABLE USER_ROLES (
    user_id VARCHAR(36) NOT NULL,
    role VARCHAR(20) NOT NULL,
    PRIMARY KEY (user_id, role),
    CONSTRAINT fk_user_roles_user_id FOREIGN KEY (user_id) REFERENCES USERS(user_id)
);

-- Create CUSTOMERS Table
CREATE TABLE CUSTOMERS (
    customer_id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    type VARCHAR(20) NOT NULL,
    company_name VARCHAR(100),
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    modified_at DATETIME2,
    CONSTRAINT uk_customer_email UNIQUE (email)
);

-- Create ORDERS Table
CREATE TABLE ORDERS (
    order_id VARCHAR(36) PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL,
    customer_id VARCHAR(36) NOT NULL,
    created_by VARCHAR(36) NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    modified_at DATETIME2,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    product_description TEXT,
    delivery_address TEXT,
    delivery_date DATE,
    CONSTRAINT uk_order_number UNIQUE (order_number),
    CONSTRAINT fk_orders_customer_id FOREIGN KEY (customer_id) REFERENCES CUSTOMERS(customer_id),
    CONSTRAINT fk_orders_created_by FOREIGN KEY (created_by) REFERENCES USERS(user_id)
);

-- Create PHOTOS Table
CREATE TABLE PHOTOS (
    photo_id VARCHAR(36) PRIMARY KEY,
    order_id VARCHAR(36) NOT NULL,
    uploaded_by VARCHAR(36) NOT NULL,
    uploaded_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    image_path VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    reviewed_by VARCHAR(36),
    reviewed_at DATETIME2,
    comments TEXT,
    template_id VARCHAR(36),
    modified_at DATETIME2,
    CONSTRAINT fk_photos_order_id FOREIGN KEY (order_id) REFERENCES ORDERS(order_id),
    CONSTRAINT fk_photos_uploaded_by FOREIGN KEY (uploaded_by) REFERENCES USERS(user_id),
    CONSTRAINT fk_photos_reviewed_by FOREIGN KEY (reviewed_by) REFERENCES USERS(user_id)
);

-- Create PHOTO_ANNOTATIONS Table
CREATE TABLE PHOTO_ANNOTATIONS (
    annotation_id VARCHAR(36) PRIMARY KEY,
    photo_id VARCHAR(36) NOT NULL,
    x_position INT NOT NULL,
    y_position INT NOT NULL,
    text TEXT NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT fk_annotations_photo_id FOREIGN KEY (photo_id) REFERENCES PHOTOS(photo_id)
);

-- Create REPORTS Table
CREATE TABLE REPORTS (
    report_id VARCHAR(36) PRIMARY KEY,
    order_id VARCHAR(36) NOT NULL,
    generated_by VARCHAR(36) NOT NULL,
    generated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    recipient_id VARCHAR(36) NOT NULL,
    format VARCHAR(20) NOT NULL DEFAULT 'PDF',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    comments TEXT,
    version INT NOT NULL DEFAULT 1,
    modified_at DATETIME2,
    CONSTRAINT fk_reports_order_id FOREIGN KEY (order_id) REFERENCES ORDERS(order_id),
    CONSTRAINT fk_reports_generated_by FOREIGN KEY (generated_by) REFERENCES USERS(user_id),
    CONSTRAINT fk_reports_recipient_id FOREIGN KEY (recipient_id) REFERENCES CUSTOMERS(customer_id)
);

-- Create REPORT_PHOTOS Table
CREATE TABLE REPORT_PHOTOS (
    report_id VARCHAR(36) NOT NULL,
    photo_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (report_id, photo_id),
    CONSTRAINT fk_report_photos_report_id FOREIGN KEY (report_id) REFERENCES REPORTS(report_id),
    CONSTRAINT fk_report_photos_photo_id FOREIGN KEY (photo_id) REFERENCES PHOTOS(photo_id)
);

-- Create indexes for better performance
CREATE INDEX idx_users_username ON USERS (username);
CREATE INDEX idx_users_email ON USERS (email);
CREATE INDEX idx_users_nfc_id ON USERS (nfc_id);
CREATE INDEX idx_users_status ON USERS (status);

CREATE INDEX idx_customers_email ON CUSTOMERS (email);
CREATE INDEX idx_customers_type ON CUSTOMERS (type);

CREATE INDEX idx_orders_order_number ON ORDERS (order_number);
CREATE INDEX idx_orders_customer_id ON ORDERS (customer_id);
CREATE INDEX idx_orders_status ON ORDERS (status);

CREATE INDEX idx_photos_order_id ON PHOTOS (order_id);
CREATE INDEX idx_photos_status ON PHOTOS (status);
CREATE INDEX idx_photos_uploaded_by ON PHOTOS (uploaded_by);

CREATE INDEX idx_reports_order_id ON REPORTS (order_id);
CREATE INDEX idx_reports_status ON REPORTS (status);
CREATE INDEX idx_reports_recipient_id ON REPORTS (recipient_id);
