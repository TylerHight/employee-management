DROP TABLE IF EXISTS users;

CREATE TABLE users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(50) DEFAULT 'USER'
);

-- Insert test data with BCrypt-hashed passwords
-- 'password123' hashed with BCrypt
INSERT INTO users (email, password, role) 
VALUES ('test@example.com', '$2a$12$FaBAyXIRevCx6kmmcUzp/.I0ZM8KGxUwc2o9Z5lKSaDVPQEAaFLoq', 'ADMIN');

-- 'password123' hashed with BCrypt
INSERT INTO users (email, password, role)
VALUES ('user@example.com', '$2a$12$f38fOBy39PNa5YjnYSXdHOjhHNuaa70K/MPsXR23V7KyES.wUfZRK', 'USER');
