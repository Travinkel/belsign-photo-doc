-- Drop existing tables that will be replaced
DROP TABLE IF EXISTS photo_documents;
DROP TABLE IF EXISTS orderAggregates;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS users;

-- Create USERS Table
CREATE TABLE USERS (
    user_id TEXT PRIMARY KEY,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    email TEXT NOT NULL,
    phone TEXT,
    name TEXT,
    nfc_id TEXT,
    status TEXT NOT NULL DEFAULT 'PENDING',
    created_at DATETIME DEFAULT (datetime('now')),
    modified_at DATETIME,
    CONSTRAINT uk_username UNIQUE (username),
    CONSTRAINT uk_email UNIQUE (email),
    CONSTRAINT uk_nfc_id UNIQUE (nfc_id)
);

-- Create USER_ROLES Table
CREATE TABLE USER_ROLES (
    user_id TEXT NOT NULL,
    role TEXT NOT NULL,
    PRIMARY KEY (user_id, role),
    CONSTRAINT fk_user_roles_user_id FOREIGN KEY (user_id) REFERENCES USERS(user_id)
);

-- Create CUSTOMERS Table
CREATE TABLE CUSTOMERS (
    customer_id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT NOT NULL,
    phone TEXT,
    type TEXT NOT NULL,
    company_name TEXT,
    created_at DATETIME DEFAULT (datetime('now')),
    modified_at DATETIME,
    CONSTRAINT uk_customer_email UNIQUE (email)
);

-- Create ORDERS Table
CREATE TABLE ORDERS (
    order_id TEXT PRIMARY KEY,
    order_number TEXT NOT NULL,
    customer_id TEXT NOT NULL,
    created_by TEXT NOT NULL,
    created_at DATETIME DEFAULT (datetime('now')),
    modified_at DATETIME,
    status TEXT NOT NULL DEFAULT 'PENDING',
    product_description TEXT,
    delivery_address TEXT,
    delivery_date DATE,
    CONSTRAINT uk_order_number UNIQUE (order_number),
    CONSTRAINT fk_orders_customer_id FOREIGN KEY (customer_id) REFERENCES CUSTOMERS(customer_id),
    CONSTRAINT fk_orders_created_by FOREIGN KEY (created_by) REFERENCES USERS(user_id)
);

-- Create PHOTOS Table
CREATE TABLE PHOTOS (
    photo_id TEXT PRIMARY KEY,
    order_id TEXT NOT NULL,
    uploaded_by TEXT NOT NULL,
    uploaded_at DATETIME DEFAULT (datetime('now')),
    image_path TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'PENDING',
    reviewed_by TEXT,
    reviewed_at DATETIME,
    comments TEXT,
    template_id TEXT,
    modified_at DATETIME,
    CONSTRAINT fk_photos_order_id FOREIGN KEY (order_id) REFERENCES ORDERS(order_id),
    CONSTRAINT fk_photos_uploaded_by FOREIGN KEY (uploaded_by) REFERENCES USERS(user_id),
    CONSTRAINT fk_photos_reviewed_by FOREIGN KEY (reviewed_by) REFERENCES USERS(user_id)
);

-- Create PHOTO_ANNOTATIONS Table
CREATE TABLE PHOTO_ANNOTATIONS (
    annotation_id TEXT PRIMARY KEY,
    photo_id TEXT NOT NULL,
    x_position INTEGER NOT NULL,
    y_position INTEGER NOT NULL,
    text TEXT NOT NULL,
    created_at DATETIME DEFAULT (datetime('now')),
    CONSTRAINT fk_annotations_photo_id FOREIGN KEY (photo_id) REFERENCES PHOTOS(photo_id)
);

-- Create REPORTS Table
CREATE TABLE REPORTS (
    report_id TEXT PRIMARY KEY,
    order_id TEXT NOT NULL,
    generated_by TEXT NOT NULL,
    generated_at DATETIME DEFAULT (datetime('now')),
    recipient_id TEXT NOT NULL,
    format TEXT NOT NULL DEFAULT 'PDF',
    status TEXT NOT NULL DEFAULT 'PENDING',
    comments TEXT,
    version INTEGER NOT NULL DEFAULT 1,
    modified_at DATETIME,
    CONSTRAINT fk_reports_order_id FOREIGN KEY (order_id) REFERENCES ORDERS(order_id),
    CONSTRAINT fk_reports_generated_by FOREIGN KEY (generated_by) REFERENCES USERS(user_id),
    CONSTRAINT fk_reports_recipient_id FOREIGN KEY (recipient_id) REFERENCES CUSTOMERS(customer_id)
);

-- Create REPORT_PHOTOS Table
CREATE TABLE REPORT_PHOTOS (
    report_id TEXT NOT NULL,
    photo_id TEXT NOT NULL,
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