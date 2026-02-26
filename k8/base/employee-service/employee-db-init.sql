-- This script can be used to load test data into the database

CREATE TABLE IF NOT EXISTS employees (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL UNIQUE,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    address VARCHAR(200),
    city VARCHAR(100),
    state VARCHAR(50),
    zip VARCHAR(10),
    cell_phone VARCHAR(15),
    home_phone VARCHAR(15),
    email VARCHAR(100) UNIQUE,
    role VARCHAR(36) NOT NULL DEFAULT 'USER'
);

-- Insert test data with explicit role
INSERT INTO employees (
    user_id, first_name, last_name, address, city, state, zip, 
    cell_phone, home_phone, email, role
) VALUES (
    '319f654b-42c2-489f-9638-fdf4a5a553b1',
    'John', 'Doe', '123 Main Street', 'Los Angeles', 'California', '90210',
    '555-123-4567', '555-987-6543', 'test@example.com', 'ADMIN'
);

-- Insert test data without specifying role (will default to 'USER')
INSERT INTO employees (
    user_id, first_name, last_name, address, city, state, zip, 
    cell_phone, home_phone, email
) VALUES (
    '7f83fbe8-ba8c-4bf4-b50a-ddf9d1aaed9c',
    'Jane', 'Smith', '456 Oak Avenue', 'New York City', 'New York', '10001',
    '5555555555', '5554443333', 'user@example.com'
);

SELECT * FROM employees;
