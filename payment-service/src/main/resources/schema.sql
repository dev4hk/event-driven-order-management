DROP TABLE IF EXISTS payment;

CREATE TABLE payment
(
    payment_id     UUID PRIMARY KEY,
    order_id       UUID           NOT NULL,
    customer_id    UUID           NOT NULL,
    amount         DECIMAL(19, 2) NOT NULL,
    payment_status VARCHAR(50)    NOT NULL,
    message        VARCHAR(255),
    created_at     TIMESTAMP      NOT NULL,
    updated_at     TIMESTAMP
);

CREATE INDEX idx_payment_order_id ON payment (order_id);
CREATE INDEX idx_payment_customer_id ON payment (customer_id);