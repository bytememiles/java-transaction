-- Seed Data Script for Transaction System
-- This script populates the database with sample data for development and testing
-- Run this after the initial schema migration (V1__initial_schema.sql)

-- Clear existing seed data (optional - comment out if you want to keep existing data)
-- DELETE FROM reconciliation_reports;
-- DELETE FROM payments;
-- DELETE FROM orders;
-- DELETE FROM inventory_transactions;
-- DELETE FROM account_transactions;
-- DELETE FROM inventory;
-- DELETE FROM products;
-- DELETE FROM merchants;
-- DELETE FROM accounts;
-- DELETE FROM users;

-- Reset sequences (if needed)
-- ALTER SEQUENCE users_id_seq RESTART WITH 1;
-- ALTER SEQUENCE merchants_id_seq RESTART WITH 1;

-- Seed Users and Accounts
INSERT INTO users (username, email, created_at, updated_at) VALUES
('john_doe', 'john.doe@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('jane_smith', 'jane.smith@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bob_wilson', 'bob.wilson@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('alice_brown', 'alice.brown@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('charlie_davis', 'charlie.davis@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (username) DO NOTHING;

-- Create accounts for users (with initial balances)
INSERT INTO accounts (user_id, balance, currency, version, created_at, updated_at)
SELECT u.id, 
       CASE 
           WHEN u.username = 'john_doe' THEN 1000.00
           WHEN u.username = 'jane_smith' THEN 500.00
           WHEN u.username = 'bob_wilson' THEN 750.00
           WHEN u.username = 'alice_brown' THEN 2000.00
           ELSE 100.00
       END,
       'USD', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM users u
WHERE u.username IN ('john_doe', 'jane_smith', 'bob_wilson', 'alice_brown', 'charlie_davis')
ON CONFLICT (user_id) DO NOTHING;

-- Seed Merchants
INSERT INTO merchants (name, account_balance, currency, version, created_at, updated_at) VALUES
('Tech Store', 0.00, 'USD', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Fashion Boutique', 0.00, 'USD', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Electronics Hub', 0.00, 'USD', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Book Emporium', 0.00, 'USD', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- Seed Products for Tech Store (merchant_id = 1)
INSERT INTO products (merchant_id, sku, name, price, currency, created_at, updated_at) VALUES
(1, 'LAPTOP-001', 'Gaming Laptop Pro', 1299.99, 'USD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'MOUSE-001', 'Wireless Gaming Mouse', 79.99, 'USD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'KEYBOARD-001', 'Mechanical Keyboard', 149.99, 'USD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'MONITOR-001', '4K Gaming Monitor', 599.99, 'USD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'HEADPHONES-001', 'Wireless Headphones', 199.99, 'USD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (merchant_id, sku) DO UPDATE SET price = EXCLUDED.price;

-- Seed Products for Fashion Boutique (merchant_id = 2)
INSERT INTO products (merchant_id, sku, name, price, currency, created_at, updated_at) VALUES
(2, 'SHIRT-001', 'Classic White Shirt', 49.99, 'USD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'JEANS-001', 'Slim Fit Jeans', 89.99, 'USD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'JACKET-001', 'Leather Jacket', 299.99, 'USD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'SHOES-001', 'Running Shoes', 129.99, 'USD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (merchant_id, sku) DO UPDATE SET price = EXCLUDED.price;

-- Seed Products for Electronics Hub (merchant_id = 3)
INSERT INTO products (merchant_id, sku, name, price, currency, created_at, updated_at) VALUES
(3, 'PHONE-001', 'Smartphone Pro', 899.99, 'USD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'TABLET-001', 'Tablet Air', 499.99, 'USD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'SMARTWATCH-001', 'Smart Watch', 249.99, 'USD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (merchant_id, sku) DO UPDATE SET price = EXCLUDED.price;

-- Seed Products for Book Emporium (merchant_id = 4)
INSERT INTO products (merchant_id, sku, name, price, currency, created_at, updated_at) VALUES
(4, 'BOOK-001', 'Programming Guide', 39.99, 'USD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'BOOK-002', 'Design Patterns', 49.99, 'USD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'BOOK-003', 'System Architecture', 59.99, 'USD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (merchant_id, sku) DO UPDATE SET price = EXCLUDED.price;

-- Seed Inventory for all products
INSERT INTO inventory (product_id, quantity, version, created_at, updated_at)
SELECT p.id, 
       CASE 
           WHEN p.merchant_id = 1 THEN 50  -- Tech Store: 50 items each
           WHEN p.merchant_id = 2 THEN 100 -- Fashion Boutique: 100 items each
           WHEN p.merchant_id = 3 THEN 30  -- Electronics Hub: 30 items each
           WHEN p.merchant_id = 4 THEN 200 -- Book Emporium: 200 items each
           ELSE 25
       END,
       0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM products p
ON CONFLICT (product_id) DO UPDATE SET quantity = EXCLUDED.quantity;

-- Seed some initial account transactions (recharges)
INSERT INTO account_transactions (account_id, transaction_type, amount, balance_before, balance_after, reference_id, created_at)
SELECT a.id, 'RECHARGE', 1000.00, 0.00, 1000.00, 'SEED-RECHARGE-' || a.id, CURRENT_TIMESTAMP
FROM accounts a
JOIN users u ON a.user_id = u.id
WHERE u.username = 'john_doe'
ON CONFLICT DO NOTHING;

INSERT INTO account_transactions (account_id, transaction_type, amount, balance_before, balance_after, reference_id, created_at)
SELECT a.id, 'RECHARGE', 500.00, 0.00, 500.00, 'SEED-RECHARGE-' || a.id, CURRENT_TIMESTAMP
FROM accounts a
JOIN users u ON a.user_id = u.id
WHERE u.username = 'jane_smith'
ON CONFLICT DO NOTHING;

-- Print summary
DO $$
DECLARE
    user_count INTEGER;
    merchant_count INTEGER;
    product_count INTEGER;
    inventory_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO user_count FROM users;
    SELECT COUNT(*) INTO merchant_count FROM merchants;
    SELECT COUNT(*) INTO product_count FROM products;
    SELECT COUNT(*) INTO inventory_count FROM inventory;
    
    RAISE NOTICE 'Seeding completed successfully!';
    RAISE NOTICE 'Users: %', user_count;
    RAISE NOTICE 'Merchants: %', merchant_count;
    RAISE NOTICE 'Products: %', product_count;
    RAISE NOTICE 'Inventory items: %', inventory_count;
END $$;

