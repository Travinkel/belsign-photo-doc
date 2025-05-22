USE
BelSign;

-- Add assigned_to column to ORDERS table in MSSQL
-- This migration adds a column to track which user an order is assigned to

-- Add the assigned_to column
ALTER TABLE ORDERS ADD assigned_to VARCHAR(36);

-- Add foreign key constraint
ALTER TABLE ORDERS ADD CONSTRAINT fk_orders_assigned_to FOREIGN KEY (assigned_to) REFERENCES USERS (user_id);

-- Create index for better performance
CREATE INDEX idx_orders_assigned_to ON ORDERS (assigned_to);
