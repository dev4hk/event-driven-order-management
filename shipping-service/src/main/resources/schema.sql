DROP TABLE IF EXISTS shipping;

CREATE TABLE shipping
(
    shipping_id     UUID PRIMARY KEY,
    order_id        UUID         NOT NULL,
    address         VARCHAR(255) NOT NULL,
    city            VARCHAR(100) NOT NULL,
    state           VARCHAR(100) NOT NULL,
    zip_code        VARCHAR(5)   NOT NULL,
    name            VARCHAR(100) NOT NULL,
    shipping_status VARCHAR(50)  NOT NULL,
    updated_at      TIMESTAMP    NOT NULL,
    message         VARCHAR(255)
);

CREATE INDEX idx_shipping_order_id ON shipping (order_id);
CREATE INDEX idx_shipping_status ON shipping (shipping_status);