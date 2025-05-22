-- Add customers for Belman
INSERT INTO CUSTOMERS (customer_id, name, email, phone, type, company_name, created_at)
VALUES
    (hex(randomblob(16)), 'Ørsted A/S', 'contact@orsted.dk', '+45 9955 1111', 'COMPANY', 'Ørsted A/S', datetime('now')),
    (hex(randomblob(16)), 'Vestas Wind Systems', 'info@vestas.com', '+45 9730 0000', 'COMPANY', 'Vestas Wind Systems A/S', datetime('now')),
    (hex(randomblob(16)), 'Maersk Oil', 'support@maersk.com', '+45 3363 3363', 'COMPANY', 'A.P. Møller - Mærsk A/S', datetime('now'));

-- Add dummy orders with real customers
INSERT INTO ORDERS (order_id, order_number, customer_id, created_by, created_at, status, product_description, delivery_address, delivery_date)
VALUES
    (hex(randomblob(16)), 'ORD-01-230701-WLD-0001',
     (SELECT customer_id FROM CUSTOMERS WHERE name = 'Ørsted A/S'),
     (SELECT user_id FROM USERS WHERE email = 'production@belman.com'), datetime('now'), 'IN_PROGRESS',
     'Expansion joints for offshore wind platform', 'Ørsted HQ, Kraftværksvej 53, 7000 Fredericia, Denmark', date('now', '+30 days')),

    (hex(randomblob(16)), 'ORD-02-230715-EXP-0002',
     (SELECT customer_id FROM CUSTOMERS WHERE name = 'Vestas Wind Systems'),
     (SELECT user_id FROM USERS WHERE email = 'production@belman.com'), datetime('now'), 'IN_PROGRESS',
     'Metal bellows for wind turbine nacelles', 'Vestas HQ, Hedeager 42, 8200 Aarhus N, Denmark', date('now', '+45 days')),

    (hex(randomblob(16)), 'ORD-03-230801-PIP-0003',
     (SELECT customer_id FROM CUSTOMERS WHERE name = 'Maersk Oil'),
     (SELECT user_id FROM USERS WHERE email = 'production@belman.com'), datetime('now'), 'IN_PROGRESS',
     'Pipe expansion joints for marine transport', 'Esplanaden 50, 1098 København K, Denmark', date('now', '+60 days'));
