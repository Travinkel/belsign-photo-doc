USE BelSign;

-- Create Orders Table
CREATE TABLE orders
(
    id                   VARCHAR(36) PRIMARY KEY,
    order_number         VARCHAR(50) NOT NULL UNIQUE,
    customer_id          VARCHAR(36) NOT NULL,
    product_description  TEXT,
    delivery_information TEXT,
    status               VARCHAR(20) NOT NULL,
    created_by           VARCHAR(36) NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    FOREIGN KEY (customer_id) REFERENCES customers (id),
    FOREIGN KEY (created_by) REFERENCES users (id)
);

-- Create Index on Order Number
CREATE INDEX idx_orders_order_number ON orders (order_number);

-- Create Index on Customer ID
CREATE INDEX idx_orders_customer_id ON orders (customer_id);

-- Create Index on Status
CREATE INDEX idx_orders_status ON orders (status);