-- Schema for Order Service
CREATE SCHEMA IF NOT EXISTS order_service;

-- Order Table
CREATE TABLE IF NOT EXISTS order_service.orders (
    order_id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    total_amount DECIMAL(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    reason TEXT,
    created_at TIMESTAMP
);

-- Order Item Table
CREATE TABLE IF NOT EXISTS order_service.order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    price DECIMAL(19, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES order_service.orders(order_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_orders_customer_id ON order_service.orders(customer_id);
CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_service.order_items(order_id);