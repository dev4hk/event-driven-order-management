-- Schema for Customer Service
CREATE SCHEMA IF NOT EXISTS customer_service;

-- Customer Table
CREATE TABLE IF NOT EXISTS customer_service.customers (
    customer_id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    active BOOLEAN DEFAULT true,
    credit_approved BOOLEAN DEFAULT false
);

-- Indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_orders_customer_id ON order_service.orders(customer_id);