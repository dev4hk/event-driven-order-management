-- Schema for Product Service
CREATE SCHEMA IF NOT EXISTS product_service;

-- Product Table
CREATE TABLE IF NOT EXISTS product_service.products (
    product_id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(19, 2) NOT NULL,
    stock INTEGER NOT NULL,
    active BOOLEAN DEFAULT true
);
