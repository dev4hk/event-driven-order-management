DROP TABLE IF EXISTS product;

CREATE TABLE product
(
    product_id  UUID PRIMARY KEY,
    name        VARCHAR(100)   NOT NULL,
    description VARCHAR(255)   NOT NULL,
    price       DECIMAL(15, 2) NOT NULL,
    stock       INT            NOT NULL,
    active      BOOLEAN        NOT NULL
);
