-- Add assigned_to column to ORDERS table
ALTER TABLE ORDERS ADD COLUMN assigned_to TEXT;

-- Add foreign key constraint for assigned_to column
CREATE TABLE ORDERS_TEMP (
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
    assigned_to TEXT,
    CONSTRAINT uk_order_number UNIQUE (order_number),
    CONSTRAINT fk_orders_customer_id FOREIGN KEY (customer_id) REFERENCES CUSTOMERS(customer_id),
    CONSTRAINT fk_orders_created_by FOREIGN KEY (created_by) REFERENCES USERS(user_id),
    CONSTRAINT fk_orders_assigned_to FOREIGN KEY (assigned_to) REFERENCES USERS(user_id)
);

-- Copy data from ORDERS to ORDERS_TEMP
INSERT INTO ORDERS_TEMP (order_id, order_number, customer_id, created_by, created_at, modified_at, status, product_description, delivery_address, delivery_date)
SELECT order_id, order_number, customer_id, created_by, created_at, modified_at, status, product_description, delivery_address, delivery_date
FROM ORDERS;

-- Drop old ORDERS table
DROP TABLE ORDERS;

-- Rename ORDERS_TEMP to ORDERS
ALTER TABLE ORDERS_TEMP RENAME TO ORDERS;

-- Recreate indexes
CREATE INDEX idx_orders_order_number ON ORDERS (order_number);
CREATE INDEX idx_orders_customer_id ON ORDERS (customer_id);
CREATE INDEX idx_orders_status ON ORDERS (status);
CREATE INDEX idx_orders_assigned_to ON ORDERS (assigned_to);