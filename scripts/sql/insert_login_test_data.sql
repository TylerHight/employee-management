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

INSERT IGNORE INTO users 
(user_id, email, password, role, password_set)
VALUES
('c1b1ce1e-1a10-4e5c-9c9f-72477a0f1111', 'alice.johnson@example.com',
'$2a$10$4RhnV/oTCJ9BI6IZe5ZkUO3k35r.i.hvipQycO.QRfL/ZnZnf/Vku', 'USER', TRUE),

('d2c2de2f-2b20-4f6d-8d9f-83588b1f2222', 'bob.williams@example.com',
'$2a$12$FaBAyXIRevCx6kmmcUzp/.I0ZM8KGxUwc2o9Z5lKSaDVPQEAaFLoq', 'ADMIN', TRUE),

('e3d3ef3a-3c30-4a7e-8b8e-94699c2f3333', 'charlie.brown@example.com',
'$2a$10$4RhnV/oTCJ9BI6IZe5ZkUO3k35r.i.hvipQycO.QRfL/ZnZnf/Vku', 'USER', TRUE),

('f4e4fa4b-4d40-4b8f-9c7d-a57aad3f4444', 'diana.miller@example.com',
'$2a$10$4RhnV/oTCJ9BI6IZe5ZkUO3k35r.i.hvipQycO.QRfL/ZnZnf/Vku', 'USER', TRUE),

('a5f5ab5c-5e50-4c9a-8b6c-b68bbe4f5555', 'ethan.davis@example.com',
'$2a$12$FaBAyXIRevCx6kmmcUzp/.I0ZM8KGxUwc2o9Z5lKSaDVPQEAaFLoq', 'ADMIN', TRUE),

('b6a6bc6d-6f60-4dab-9a5b-c79ccf5f6666', 'fiona.garcia@example.com',
'$2a$10$4RhnV/oTCJ9BI6IZe5ZkUO3k35r.i.hvipQycO.QRfL/ZnZnf/Vku', 'USER', TRUE),

('c7b7cd7e-7a70-4ebc-8f4a-d80ddf6f7777', 'george.martinez@example.com',
'$2a$10$4RhnV/oTCJ9BI6IZe5ZkUO3k35r.i.hvipQycO.QRfL/ZnZnf/Vku', 'USER', TRUE),

('d8c8de8f-8b80-4fcd-9e39-e91eef7f8888', 'hannah.lopez@example.com',
'$2a$10$4RhnV/oTCJ9BI6IZe5ZkUO3k35r.i.hvipQycO.QRfL/ZnZnf/Vku', 'USER', TRUE),

('e9d9ef9a-9c90-4aed-8d28-fa2ffg8g9999', 'ian.gonzalez@example.com',
'$2a$12$FaBAyXIRevCx6kmmcUzp/.I0ZM8KGxUwc2o9Z5lKSaDVPQEAaFLoq', 'ADMIN', TRUE),

('f0e0fa0b-0d01-4bfe-9b17-ab3hhg9h0000', 'julia.wilson@example.com',
'$2a$10$4RhnV/oTCJ9BI6IZe5ZkUO3k35r.i.hvipQycO.QRfL/ZnZnf/Vku', 'USER', TRUE),

('11111111-aaaa-4aaa-aaaa-111111111111', 'kevin.anderson@example.com',
'$2a$10$4RhnV/oTCJ9BI6IZe5ZkUO3k35r.i.hvipQycO.QRfL/ZnZnf/Vku', 'USER', TRUE),

('22222222-bbbb-4bbb-bbbb-222222222222', 'laura.thomas@example.com',
'$2a$10$4RhnV/oTCJ9BI6IZe5ZkUO3k35r.i.hvipQycO.QRfL/ZnZnf/Vku', 'USER', TRUE),

('33333333-cccc-4ccc-cccc-333333333333', 'michael.taylor@example.com',
'$2a$12$FaBAyXIRevCx6kmmcUzp/.I0ZM8KGxUwc2o9Z5lKSaDVPQEAaFLoq', 'ADMIN', TRUE),

('44444444-dddd-4ddd-dddd-444444444444', 'nina.moore@example.com',
'$2a$10$4RhnV/oTCJ9BI6IZe5ZkUO3k35r.i.hvipQycO.QRfL/ZnZnf/Vku', 'USER', TRUE),

('55555555-eeee-4eee-eeee-555555555555', 'oscar.jackson@example.com',
'$2a$10$4RhnV/oTCJ9BI6IZe5ZkUO3k35r.i.hvipQycO.QRfL/ZnZnf/Vku', 'USER', TRUE);

INSERT IGNORE INTO users 
(user_id, email, password, role, password_set)
VALUES
('66666666-ffff-4fff-ffff-666666666666', 'paula.martin@example.com',
'$2a$12$FaBAyXIRevCx6kmmcUzp/.I0ZM8KGxUwc2o9Z5lKSaDVPQEAaFLoq', 'ADMIN', TRUE),

('77777777-1111-4111-8111-777777777777', 'quincy.lee@example.com',
'$2a$10$4RhnV/oTCJ9BI6IZe5ZkUO3k35r.i.hvipQycO.QRfL/ZnZnf/Vku', 'USER', TRUE),

('88888888-2222-4222-8222-888888888888', 'rachel.perez@example.com',
'$2a$10$4RhnV/oTCJ9BI6IZe5ZkUO3k35r.i.hvipQycO.QRfL/ZnZnf/Vku', 'USER', TRUE),

('99999999-3333-4333-8333-999999999999', 'samuel.white@example.com',
'$2a$12$FaBAyXIRevCx6kmmcUzp/.I0ZM8KGxUwc2o9Z5lKSaDVPQEAaFLoq', 'ADMIN', TRUE),

('aaaaaaaa-4444-4444-8444-aaaaaaaaaaaa', 'tina.harris@example.com',
'$2a$10$4RhnV/oTCJ9BI6IZe5ZkUO3k35r.i.hvipQycO.QRfL/ZnZnf/Vku', 'USER', TRUE),

('bbbbbbbb-5555-4555-8555-bbbbbbbbbbbb', 'uma.young@example.com',
'$2a$10$4RhnV/oTCJ9BI6IZe5ZkUO3k35r.i.hvipQycO.QRfL/ZnZnf/Vku', 'USER', TRUE),

('cccccccc-6666-4666-8666-cccccccccccc', 'victor.king@example.com',
'$2a$12$FaBAyXIRevCx6kmmcUzp/.I0ZM8KGxUwc2o9Z5lKSaDVPQEAaFLoq', 'ADMIN', TRUE),

('dddddddd-7777-4777-8777-dddddddddddd', 'wendy.scott@example.com',
'$2a$10$4RhnV/oTCJ9BI6IZe5ZkUO3k35r.i.hvipQycO.QRfL/ZnZnf/Vku', 'USER', TRUE),

('eeeeeeee-8888-4888-8888-eeeeeeeeeeee', 'xavier.adams@example.com',
'$2a$10$4RhnV/oTCJ9BI6IZe5ZkUO3k35r.i.hvipQycO.QRfL/ZnZnf/Vku', 'USER', TRUE),

('ffffffff-9999-4999-8999-ffffffffffff', 'yvonne.baker@example.com',
'$2a$12$FaBAyXIRevCx6kmmcUzp/.I0ZM8KGxUwc2o9Z5lKSaDVPQEAaFLoq', 'ADMIN', TRUE),

('12121212-aaaa-4aaa-8aaa-121212121212', 'zach.nelson@example.com',
'$2a$10$4RhnV/oTCJ9BI6IZe5ZkUO3k35r.i.hvipQycO.QRfL/ZnZnf/Vku', 'USER', TRUE),

('23232323-bbbb-4bbb-8bbb-232323232323', 'aaron.carter@example.com',
'$2a$10$4RhnV/oTCJ9BI6IZe5ZkUO3k35r.i.hvipQycO.QRfL/ZnZnf/Vku', 'USER', TRUE),

('34343434-cccc-4ccc-8ccc-343434343434', 'bella.mitchell@example.com',
'$2a$12$FaBAyXIRevCx6kmmcUzp/.I0ZM8KGxUwc2o9Z5lKSaDVPQEAaFLoq', 'ADMIN', TRUE),

('45454545-dddd-4ddd-8ddd-454545454545', 'caleb.perez@example.com',
'$2a$10$4RhnV/oTCJ9BI6IZe5ZkUO3k35r.i.hvipQycO.QRfL/ZnZnf/Vku', 'USER', TRUE),

('56565656-eeee-4eee-8eee-565656565656', 'dana.roberts@example.com',
'$2a$10$4RhnV/oTCJ9BI6IZe5ZkUO3k35r.i.hvipQycO.QRfL/ZnZnf/Vku', 'USER', TRUE);

INSERT IGNORE INTO users 
(user_id, email, password, role, password_set)
VALUES
('67676767-ffff-4fff-8fff-676767676767', 'eli.turner@example.com',
'$2a$12$FaBAyXIRevCx6kmmcUzp/.I0ZM8KGxUwc2o9Z5lKSaDVPQEAaFLoq', 'ADMIN', TRUE),

('78787878-1111-4111-8111-787878787878', 'faith.phillips@example.com',
'$2a$10$4RhnV/oTCJ9BI6IZe5ZkUO3k35r.i.hvipQycO.QRfL/ZnZnf/Vku', 'USER', TRUE),

('89898989-2222-4222-8222-898989898989', 'gavin.campbell@example.com',
'$2a$10$4RhnV/oTCJ9BI6IZe5ZkUO3k35r.i.hvipQycO.QRfL/ZnZnf/Vku', 'USER', TRUE),

('90909090-3333-4333-8333-909090909090', 'hailey.parker@example.com',
'$2a$12$FaBAyXIRevCx6kmmcUzp/.I0ZM8KGxUwc2o9Z5lKSaDVPQEAaFLoq', 'ADMIN', TRUE),

('01010101-4444-4444-8444-010101010101', 'isaac.edwards@example.com',
'$2a$10$4RhnV/oTCJ9BI6IZe5ZkUO3k35r.i.hvipQycO.QRfL/ZnZnf/Vku', 'USER', TRUE),

('02020202-5555-4555-8555-020202020202', 'jenna.collins@example.com',
'$2a$10$4RhnV/oTCJ9BI6IZe5ZkUO3k35r.i.hvipQycO.QRfL/ZnZnf/Vku', 'USER', TRUE),

('03030303-6666-4666-8666-030303030303', 'kyle.stewart@example.com',
'$2a$12$FaBAyXIRevCx6kmmcUzp/.I0ZM8KGxUwc2o9Z5lKSaDVPQEAaFLoq', 'ADMIN', TRUE),

('04040404-7777-4777-8777-040404040404', 'lily.sanchez@example.com',
'$2a$10$4RhnV/oTCJ9BI6IZe5ZkUO3k35r.i.hvipQycO.QRfL/ZnZnf/Vku', 'USER', TRUE),

('05050505-8888-4888-8888-050505050505', 'miles.morris@example.com',
'$2a$10$4RhnV/oTCJ9BI6IZe5ZkUO3k35r.i.hvipQycO.QRfL/ZnZnf/Vku', 'USER', TRUE),

('06060606-9999-4999-8999-060606060606', 'nora.rogers@example.com',
'$2a$12$FaBAyXIRevCx6kmmcUzp/.I0ZM8KGxUwc2o9Z5lKSaDVPQEAaFLoq', 'ADMIN', TRUE),

('07070707-aaaa-4aaa-8aaa-070707070707', 'owen.cook@example.com',
'$2a$10$4RhnV/oTCJ9BI6IZe5ZkUO3k35r.i.hvipQycO.QRfL/ZnZnf/Vku', 'USER', TRUE),

('08080808-bbbb-4bbb-8bbb-080808080808', 'piper.murphy@example.com',
'$2a$10$4RhnV/oTCJ9BI6IZe5ZkUO3k35r.i.hvipQycO.QRfL/ZnZnf/Vku', 'USER', TRUE),

('09090909-cccc-4ccc-8ccc-090909090909', 'quinn.bailey@example.com',
'$2a$12$FaBAyXIRevCx6kmmcUzp/.I0ZM8KGxUwc2o9Z5lKSaDVPQEAaFLoq', 'ADMIN', TRUE),

('10101010-dddd-4ddd-8ddd-101010101010', 'riley.rivera@example.com',
'$2a$10$4RhnV/oTCJ9BI6IZe5ZkUO3k35r.i.hvipQycO.QRfL/ZnZnf/Vku', 'USER', TRUE),

('11121212-eeee-4eee-8eee-111212121212', 'sophie.reed@example.com',
'$2a$10$4RhnV/oTCJ9BI6IZe5ZkUO3k35r.i.hvipQycO.QRfL/ZnZnf/Vku', 'USER', TRUE);

INSERT IGNORE INTO users 
(user_id, email, password, role, password_set)
VALUES
('12131313-ffff-4fff-8fff-121313131313', 'trent.kelly@example.com',
'$2a$12$FaBAyXIRevCx6kmmcUzp/.I0ZM8KGxUwc2o9Z5lKSaDVPQEAaFLoq', 'ADMIN', TRUE);

INSERT IGNORE INTO users 
(user_id, email, password, role, password_set)
VALUES
('13141414-1111-4111-8111-131414141414', 'ursula.howard@example.com',
'$2a$10$4RhnV/oTCJ9BI6IZe5ZkUO3k35r.i.hvipQycO.QRfL/ZnZnf/Vku', 'USER', TRUE);

INSERT IGNORE INTO users 
(user_id, email, password, role, password_set)
VALUES
('14151515-2222-4222-8222-141515151515', 'vince.ward@example.com',
'$2a$10$4RhnV/oTCJ9BI6IZe5ZkUO3k35r.i.hvipQycO.QRfL/ZnZnf/Vku', 'USER', TRUE);
