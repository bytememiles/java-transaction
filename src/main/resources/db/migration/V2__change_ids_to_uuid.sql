-- Migration to change all primary keys and foreign keys from BIGSERIAL/BIGINT to UUID

-- Drop all foreign key constraints first
ALTER TABLE accounts DROP CONSTRAINT IF EXISTS accounts_user_id_fkey;
ALTER TABLE account_transactions DROP CONSTRAINT IF EXISTS account_transactions_account_id_fkey;
ALTER TABLE products DROP CONSTRAINT IF EXISTS products_merchant_id_fkey;
ALTER TABLE inventory DROP CONSTRAINT IF EXISTS inventory_product_id_fkey;
ALTER TABLE inventory_transactions DROP CONSTRAINT IF EXISTS inventory_transactions_inventory_id_fkey;
ALTER TABLE orders DROP CONSTRAINT IF EXISTS orders_user_id_fkey;
ALTER TABLE orders DROP CONSTRAINT IF EXISTS orders_product_id_fkey;
ALTER TABLE orders DROP CONSTRAINT IF EXISTS orders_merchant_id_fkey;
ALTER TABLE payments DROP CONSTRAINT IF EXISTS payments_order_id_fkey;
ALTER TABLE reconciliation_reports DROP CONSTRAINT IF EXISTS reconciliation_reports_merchant_id_fkey;

-- Drop indexes that reference the columns we're changing (ID columns)
-- We need to drop all indexes that reference ID columns since they'll change type
DROP INDEX IF EXISTS idx_orders_user_id;
DROP INDEX IF EXISTS idx_orders_merchant_id;
DROP INDEX IF EXISTS idx_account_transactions_account_id;
DROP INDEX IF EXISTS idx_inventory_transactions_inventory_id;
DROP INDEX IF EXISTS idx_payments_order_id;
DROP INDEX IF EXISTS idx_reconciliation_reports_merchant_id;
-- Note: We don't drop indexes on non-ID columns (status, order_number, created_at, report_date)
-- as they don't change type and can remain

-- Drop unique constraints
ALTER TABLE accounts DROP CONSTRAINT IF EXISTS uk_accounts_user_id;
ALTER TABLE products DROP CONSTRAINT IF EXISTS uk_products_merchant_sku;
ALTER TABLE inventory DROP CONSTRAINT IF EXISTS uk_inventory_product_id;
ALTER TABLE orders DROP CONSTRAINT IF EXISTS uk_orders_order_number;
ALTER TABLE reconciliation_reports DROP CONSTRAINT IF EXISTS uk_reconciliation_merchant_date;

-- Change primary keys to UUID
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_pkey;
ALTER TABLE users ALTER COLUMN id DROP DEFAULT;
ALTER TABLE users ALTER COLUMN id TYPE UUID USING gen_random_uuid();
ALTER TABLE users ALTER COLUMN id SET DEFAULT gen_random_uuid();
ALTER TABLE users ADD PRIMARY KEY (id);

ALTER TABLE accounts DROP CONSTRAINT IF EXISTS accounts_pkey;
ALTER TABLE accounts ALTER COLUMN id DROP DEFAULT;
ALTER TABLE accounts ALTER COLUMN id TYPE UUID USING gen_random_uuid();
ALTER TABLE accounts ALTER COLUMN id SET DEFAULT gen_random_uuid();
ALTER TABLE accounts ADD PRIMARY KEY (id);

ALTER TABLE account_transactions DROP CONSTRAINT IF EXISTS account_transactions_pkey;
ALTER TABLE account_transactions ALTER COLUMN id DROP DEFAULT;
ALTER TABLE account_transactions ALTER COLUMN id TYPE UUID USING gen_random_uuid();
ALTER TABLE account_transactions ALTER COLUMN id SET DEFAULT gen_random_uuid();
ALTER TABLE account_transactions ADD PRIMARY KEY (id);

ALTER TABLE merchants DROP CONSTRAINT IF EXISTS merchants_pkey;
ALTER TABLE merchants ALTER COLUMN id DROP DEFAULT;
ALTER TABLE merchants ALTER COLUMN id TYPE UUID USING gen_random_uuid();
ALTER TABLE merchants ALTER COLUMN id SET DEFAULT gen_random_uuid();
ALTER TABLE merchants ADD PRIMARY KEY (id);

ALTER TABLE products DROP CONSTRAINT IF EXISTS products_pkey;
ALTER TABLE products ALTER COLUMN id DROP DEFAULT;
ALTER TABLE products ALTER COLUMN id TYPE UUID USING gen_random_uuid();
ALTER TABLE products ALTER COLUMN id SET DEFAULT gen_random_uuid();
ALTER TABLE products ADD PRIMARY KEY (id);

ALTER TABLE inventory DROP CONSTRAINT IF EXISTS inventory_pkey;
ALTER TABLE inventory ALTER COLUMN id DROP DEFAULT;
ALTER TABLE inventory ALTER COLUMN id TYPE UUID USING gen_random_uuid();
ALTER TABLE inventory ALTER COLUMN id SET DEFAULT gen_random_uuid();
ALTER TABLE inventory ADD PRIMARY KEY (id);

ALTER TABLE inventory_transactions DROP CONSTRAINT IF EXISTS inventory_transactions_pkey;
ALTER TABLE inventory_transactions ALTER COLUMN id DROP DEFAULT;
ALTER TABLE inventory_transactions ALTER COLUMN id TYPE UUID USING gen_random_uuid();
ALTER TABLE inventory_transactions ALTER COLUMN id SET DEFAULT gen_random_uuid();
ALTER TABLE inventory_transactions ADD PRIMARY KEY (id);

ALTER TABLE orders DROP CONSTRAINT IF EXISTS orders_pkey;
ALTER TABLE orders ALTER COLUMN id DROP DEFAULT;
ALTER TABLE orders ALTER COLUMN id TYPE UUID USING gen_random_uuid();
ALTER TABLE orders ALTER COLUMN id SET DEFAULT gen_random_uuid();
ALTER TABLE orders ADD PRIMARY KEY (id);

ALTER TABLE payments DROP CONSTRAINT IF EXISTS payments_pkey;
ALTER TABLE payments ALTER COLUMN id DROP DEFAULT;
ALTER TABLE payments ALTER COLUMN id TYPE UUID USING gen_random_uuid();
ALTER TABLE payments ALTER COLUMN id SET DEFAULT gen_random_uuid();
ALTER TABLE payments ADD PRIMARY KEY (id);

ALTER TABLE reconciliation_reports DROP CONSTRAINT IF EXISTS reconciliation_reports_pkey;
ALTER TABLE reconciliation_reports ALTER COLUMN id DROP DEFAULT;
ALTER TABLE reconciliation_reports ALTER COLUMN id TYPE UUID USING gen_random_uuid();
ALTER TABLE reconciliation_reports ALTER COLUMN id SET DEFAULT gen_random_uuid();
ALTER TABLE reconciliation_reports ADD PRIMARY KEY (id);

-- Change foreign key columns to UUID
ALTER TABLE accounts ALTER COLUMN user_id TYPE UUID USING gen_random_uuid();
ALTER TABLE account_transactions ALTER COLUMN account_id TYPE UUID USING gen_random_uuid();
ALTER TABLE products ALTER COLUMN merchant_id TYPE UUID USING gen_random_uuid();
ALTER TABLE inventory ALTER COLUMN product_id TYPE UUID USING gen_random_uuid();
ALTER TABLE inventory_transactions ALTER COLUMN inventory_id TYPE UUID USING gen_random_uuid();
ALTER TABLE orders ALTER COLUMN user_id TYPE UUID USING gen_random_uuid();
ALTER TABLE orders ALTER COLUMN product_id TYPE UUID USING gen_random_uuid();
ALTER TABLE orders ALTER COLUMN merchant_id TYPE UUID USING gen_random_uuid();
ALTER TABLE payments ALTER COLUMN order_id TYPE UUID USING gen_random_uuid();
ALTER TABLE reconciliation_reports ALTER COLUMN merchant_id TYPE UUID USING gen_random_uuid();

-- Recreate foreign key constraints
ALTER TABLE accounts ADD CONSTRAINT accounts_user_id_fkey 
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE account_transactions ADD CONSTRAINT account_transactions_account_id_fkey 
    FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE;
ALTER TABLE products ADD CONSTRAINT products_merchant_id_fkey 
    FOREIGN KEY (merchant_id) REFERENCES merchants(id) ON DELETE CASCADE;
ALTER TABLE inventory ADD CONSTRAINT inventory_product_id_fkey 
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE;
ALTER TABLE inventory_transactions ADD CONSTRAINT inventory_transactions_inventory_id_fkey 
    FOREIGN KEY (inventory_id) REFERENCES inventory(id) ON DELETE CASCADE;
ALTER TABLE orders ADD CONSTRAINT orders_user_id_fkey 
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT;
ALTER TABLE orders ADD CONSTRAINT orders_product_id_fkey 
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT;
ALTER TABLE orders ADD CONSTRAINT orders_merchant_id_fkey 
    FOREIGN KEY (merchant_id) REFERENCES merchants(id) ON DELETE RESTRICT;
ALTER TABLE payments ADD CONSTRAINT payments_order_id_fkey 
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE;
ALTER TABLE reconciliation_reports ADD CONSTRAINT reconciliation_reports_merchant_id_fkey 
    FOREIGN KEY (merchant_id) REFERENCES merchants(id) ON DELETE CASCADE;

-- Recreate unique constraints
ALTER TABLE accounts ADD CONSTRAINT uk_accounts_user_id UNIQUE(user_id);
ALTER TABLE products ADD CONSTRAINT uk_products_merchant_sku UNIQUE(merchant_id, sku);
ALTER TABLE inventory ADD CONSTRAINT uk_inventory_product_id UNIQUE(product_id);
ALTER TABLE orders ADD CONSTRAINT uk_orders_order_number UNIQUE(order_number);
ALTER TABLE reconciliation_reports ADD CONSTRAINT uk_reconciliation_merchant_date UNIQUE(merchant_id, report_date);

-- Recreate indexes on ID columns (now UUID type)
-- Indexes on non-ID columns (status, order_number, created_at, report_date) already exist from V1
CREATE INDEX IF NOT EXISTS idx_orders_user_id ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_merchant_id ON orders(merchant_id);
CREATE INDEX IF NOT EXISTS idx_account_transactions_account_id ON account_transactions(account_id);
CREATE INDEX IF NOT EXISTS idx_inventory_transactions_inventory_id ON inventory_transactions(inventory_id);
CREATE INDEX IF NOT EXISTS idx_payments_order_id ON payments(order_id);
CREATE INDEX IF NOT EXISTS idx_reconciliation_reports_merchant_id ON reconciliation_reports(merchant_id);

