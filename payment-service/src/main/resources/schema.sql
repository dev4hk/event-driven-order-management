-- Schema for Payment Service
CREATE SCHEMA IF NOT EXISTS payment_service;

-- Payment Table
CREATE TABLE IF NOT EXISTS payment_service.payments (
    payment_id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    reason TEXT,
    created_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_payments_order_id ON payment_service.payments(order_id);
CREATE INDEX IF NOT EXISTS idx_payments_customer_id ON payment_service.payments(customer_id);