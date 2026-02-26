USE login_db;

DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- NEW: stable cross-service user identifier from registration-service
    user_id CHAR(36) NOT NULL UNIQUE,

    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'USER',

    reset_token VARCHAR(255) UNIQUE,
    reset_token_expiry DATETIME,
    password_set BOOLEAN NOT NULL DEFAULT TRUE
);

-- Insert test data with BCrypt-hashed passwords
-- NOTE: user_id values here are just example UUIDs
-- 'password123' hashed with BCrypt
INSERT INTO users (user_id, email, password, role, password_set) 
VALUES ('319f654b-42c2-489f-9638-fdf4a5a553b1', 'test@example.com', '$2a$12$FaBAyXIRevCx6kmmcUzp/.I0ZM8KGxUwc2o9Z5lKSaDVPQEAaFLoq', 'ADMIN', TRUE);

-- 'password123' hashed with BCrypt
INSERT INTO users (user_id, email, password, role, password_set)
VALUES ('7f83fbe8-ba8c-4bf4-b50a-ddf9d1aaed9c', 'user@example.com', '$2a$10$4RhnV/oTCJ9BI6IZe5ZkUO3k35r.i.hvipQycO.QRfL/ZnZnf/Vku', 'USER', TRUE);
