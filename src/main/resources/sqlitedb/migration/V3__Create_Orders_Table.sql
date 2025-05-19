-- Create Orders Table
CREATE TABLE IF NOT EXISTS orderAggregates
(
    id                   TEXT PRIMARY KEY,
    order_number         TEXT NOT NULL UNIQUE,
    customer_id          TEXT NOT NULL,
    product_description  TEXT,
    delivery_information TEXT,
    status               TEXT NOT NULL,
    created_by           TEXT NOT NULL,
    created_at           TEXT   NOT NULL DEFAULT (datetime('now')),
    updated_at           TEXT   NOT NULL DEFAULT (datetime('now')),
    FOREIGN KEY (customer_id) REFERENCES customers (id),
    FOREIGN KEY (created_by) REFERENCES users (id)
);

-- Create Index on OrderBusiness Number
CREATE INDEX IF NOT EXISTS idx_orders_order_number ON orderAggregates (order_number);

-- Create Index on Customer ID
CREATE INDEX IF NOT EXISTS idx_orders_customer_id ON orderAggregates (customer_id);

-- Create Index on Status
CREATE INDEX IF NOT EXISTS idx_orders_status ON orderAggregates (status);
