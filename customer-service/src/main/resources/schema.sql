DROP TABLE IF EXISTS customer;

CREATE TABLE customer
(
    customer_id     UUID PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    email           VARCHAR(255) NOT NULL UNIQUE,
    active          BOOLEAN      NOT NULL DEFAULT FALSE,
    credit_approved BOOLEAN      NOT NULL DEFAULT FALSE
);
