-- Schema for Shipping Service
CREATE SCHEMA IF NOT EXISTS shipping_service;

-- Shipping Table
CREATE TABLE IF NOT EXISTS shipping_service.shipping (
    shipping_id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    shipped_at TIMESTAMP,
    updated_at TIMESTAMP,
    delivered_at TIMESTAMP
);

-- Indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_shipping_order_id ON shipping_service.shipping(order_id);
