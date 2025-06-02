CREATE SCHEMA IF NOT EXISTS order_service;

CREATE TABLE IF NOT EXISTS order_service.orders (
    order_id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    customer_name VARCHAR(100),
    customer_email VARCHAR(100),
    address VARCHAR(150) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    zip_code VARCHAR(5) NOT NULL,
    payment_id UUID,
    payment_status VARCHAR(50),
    shipping_id UUID,
    shipping_status VARCHAR(50),
    total_amount DECIMAL(19, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    reason VARCHAR(255),
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS order_service.order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    price DECIMAL(19, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES order_service.orders(order_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_orders_customer_id ON order_service.orders(customer_id);
CREATE INDEX IF NOT EXISTS idx_orders_payment_id ON order_service.orders(payment_id);
CREATE INDEX IF NOT EXISTS idx_orders_shipping_id ON order_service.orders(shipping_id);
CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_service.order_items(order_id);
