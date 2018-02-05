CREATE TABLE customer_address (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE CASCADE,
    street VARCHAR(255) NOT NULL,
    house_nr VARCHAR(20) NOT NULL,
    city VARCHAR(255) NOT NULL,
    zip VARCHAR(20) NOT NULL,
);