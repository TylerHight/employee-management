USE employee_db;

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

INSERT IGNORE INTO employees (
    user_id, first_name, last_name, address, city, state, zip, 
    cell_phone, home_phone, email, role
) VALUES (
    '319f654b-42c2-489f-9638-fdf4a5a553b1',
    'John', 'Doe', '123 Main Street', 'Los Angeles', 'California', '90210',
    '555-123-4567', '555-987-6543', 'test@example.com', 'ADMIN'
);

-- Insert test data without specifying role (will default to 'USER')
INSERT IGNORE INTO employees (
    user_id, first_name, last_name, address, city, state, zip, 
    cell_phone, home_phone, email, role
) VALUES (
    '7f83fbe8-ba8c-4bf4-b50a-ddf9d1aaed9c',
    'Jane', 'Smith', '456 Oak Avenue', 'New York City', 'New York', '10001',
    '5555555555', '5554443333', 'user@example.com', 'USER'
);

INSERT IGNORE INTO employees 
(user_id, first_name, last_name, address, city, state, zip, 
 cell_phone, home_phone, email, role)
VALUES
('c1b1ce1e-1a10-4e5c-9c9f-72477a0f1111', 'Alice', 'Johnson', '101 Maple St', 'Denver', 'Colorado', '80201', '5551110001', '5551110002', 'alice.johnson@example.com', 'USER'),
('d2c2de2f-2b20-4f6d-8d9f-83588b1f2222', 'Bob', 'Williams', '202 Pine Ave', 'Seattle', 'Washington', '98101', '5552220001', '5552220002', 'bob.williams@example.com', 'ADMIN'),
('e3d3ef3a-3c30-4a7e-8b8e-94699c2f3333', 'Charlie', 'Brown', '303 Oak Blvd', 'Miami', 'Florida', '33101', '5553330001', '5553330002', 'charlie.brown@example.com', 'USER'),
('f4e4fa4b-4d40-4b8f-9c7d-a57aad3f4444', 'Diana', 'Miller', '404 Birch Dr', 'Houston', 'Texas', '77001', '5554440001', '5554440002', 'diana.miller@example.com', 'USER'),
('a5f5ab5c-5e50-4c9a-8b6c-b68bbe4f5555', 'Ethan', 'Davis', '505 Spruce Ct', 'Boston', 'Massachusetts', '02101', '5555550001', '5555550002', 'ethan.davis@example.com', 'ADMIN'),
('b6a6bc6d-6f60-4dab-9a5b-c79ccf5f6666', 'Fiona', 'Garcia', '606 Cedar Way', 'Phoenix', 'Arizona', '85001', '5556660001', '5556660002', 'fiona.garcia@example.com', 'USER'),
('c7b7cd7e-7a70-4ebc-8f4a-d80ddf6f7777', 'George', 'Martinez', '707 Willow Ln', 'Chicago', 'Illinois', '60601', '5557770001', '5557770002', 'george.martinez@example.com', 'USER'),
('d8c8de8f-8b80-4fcd-9e39-e91eef7f8888', 'Hannah', 'Lopez', '808 Aspen Rd', 'San Diego', 'California', '92101', '5558880001', '5558880002', 'hannah.lopez@example.com', 'USER'),
('e9d9ef9a-9c90-4aed-8d28-fa2ffg8g9999', 'Ian', 'Gonzalez', '909 Redwood Pl', 'Portland', 'Oregon', '97035', '5559990001', '5559990002', 'ian.gonzalez@example.com', 'ADMIN'),
('f0e0fa0b-0d01-4bfe-9b17-ab3hhg9h0000', 'Julia', 'Wilson', '111 Elm St', 'Atlanta', 'Georgia', '30301', '5550101001', '5550101002', 'julia.wilson@example.com', 'USER'),
('11111111-aaaa-4aaa-aaaa-111111111111', 'Kevin', 'Anderson', '222 Poplar St', 'Cleveland', 'Ohio', '44101', '5551212001', '5551212002', 'kevin.anderson@example.com', 'USER'),
('22222222-bbbb-4bbb-bbbb-222222222222', 'Laura', 'Thomas', '333 Hickory St', 'Dallas', 'Texas', '75201', '5552323001', '5552323002', 'laura.thomas@example.com', 'USER'),
('33333333-cccc-4ccc-cccc-333333333333', 'Michael', 'Taylor', '444 Sycamore St', 'Austin', 'Texas', '73301', '5553434001', '5553434002', 'michael.taylor@example.com', 'ADMIN'),
('44444444-dddd-4ddd-dddd-444444444444', 'Nina', 'Moore', '555 Fir St', 'Columbus', 'Ohio', '43004', '5554545001', '5554545002', 'nina.moore@example.com', 'USER'),
('55555555-eeee-4eee-eeee-555555555555', 'Oscar', 'Jackson', '666 Walnut St', 'Charlotte', 'North Carolina', '28202', '5555656001', '5555656002', 'oscar.jackson@example.com', 'USER'),
('66666666-ffff-4fff-ffff-666666666666', 'Paula', 'Martin', '777 Chestnut St', 'San Antonio', 'Texas', '78201', '5556767001', '5556767002', 'paula.martin@example.com', 'ADMIN'),
('77777777-1111-4111-8111-777777777777', 'Quincy', 'Lee', '888 Dogwood St', 'Orlando', 'Florida', '32801', '5557878001', '5557878002', 'quincy.lee@example.com', 'USER'),
('88888888-2222-4222-8222-888888888888', 'Rachel', 'Perez', '999 Pinecrest Ct', 'Las Vegas', 'Nevada', '89101', '5558989001', '5558989002', 'rachel.perez@example.com', 'USER'),
('99999999-3333-4333-8333-999999999999', 'Samuel', 'White', '1212 Brookside Dr', 'Salt Lake City', 'Utah', '84101', '5559090001', '5559090002', 'samuel.white@example.com', 'ADMIN'),
('aaaaaaaa-4444-4444-8444-aaaaaaaaaaaa', 'Tina', 'Harris', '1313 Meadow Ln', 'Minneapolis', 'Minnesota', '55401', '5551112221', '5551112222', 'tina.harris@example.com', 'USER'),
('bbbbbbbb-5555-4555-8555-bbbbbbbbbbbb', 'Uma', 'Young', '1414 Forest Hill Dr', 'Nashville', 'Tennessee', '37201', '5552221111', '5552221112', 'uma.young@example.com', 'USER'),
('cccccccc-6666-4666-8666-cccccccccccc', 'Victor', 'King', '1515 River Rd', 'Cincinnati', 'Ohio', '45202', '5553331111', '5553331112', 'victor.king@example.com', 'ADMIN'),
('dddddddd-7777-4777-8777-dddddddddddd', 'Wendy', 'Scott', '1616 Lakeview Blvd', 'St. Louis', 'Missouri', '63101', '5554441111', '5554441112', 'wendy.scott@example.com', 'USER'),
('eeeeeeee-8888-4888-8888-eeeeeeeeeeee', 'Xavier', 'Adams', '1717 Highland Ave', 'Detroit', 'Michigan', '48201', '5555551111', '5555551112', 'xavier.adams@example.com', 'USER'),
('ffffffff-9999-4999-8999-ffffffffffff', 'Yvonne', 'Baker', '1818 Glenwood St', 'Baltimore', 'Maryland', '21201', '5556661111', '5556661112', 'yvonne.baker@example.com', 'ADMIN'),
('12121212-aaaa-4aaa-8aaa-121212121212', 'Zach', 'Nelson', '1919 Parkside Dr', 'Milwaukee', 'Wisconsin', '53201', '5557771111', '5557771112', 'zach.nelson@example.com', 'USER'),
('23232323-bbbb-4bbb-8bbb-232323232323', 'Aaron', 'Carter', '2020 Valley Rd', 'Boise', 'Idaho', '83701', '5558881111', '5558881112', 'aaron.carter@example.com', 'USER'),
('34343434-cccc-4ccc-8ccc-343434343434', 'Bella', 'Mitchell', '2121 Ridge Ave', 'Raleigh', 'North Carolina', '27601', '5559991111', '5559991112', 'bella.mitchell@example.com', 'ADMIN'),
('45454545-dddd-4ddd-8ddd-454545454545', 'Caleb', 'Perez', '2222 Summit St', 'Richmond', 'Virginia', '23220', '5550102001', '5550102002', 'caleb.perez@example.com', 'USER'),
('56565656-eeee-4eee-8eee-565656565656', 'Dana', 'Roberts', '2323 Cedar Grove Rd', 'Honolulu', 'Hawaii', '96801', '5550203001', '5550203002', 'dana.roberts@example.com', 'USER'),
('67676767-ffff-4fff-8fff-676767676767', 'Eli', 'Turner', '2424 Hillcrest Ln', 'Des Moines', 'Iowa', '50309', '5550304001', '5550304002', 'eli.turner@example.com', 'ADMIN'),
('78787878-1111-4111-8111-787878787878', 'Faith', 'Phillips', '2525 Garden St', 'Birmingham', 'Alabama', '35203', '5550405001', '5550405002', 'faith.phillips@example.com', 'USER'),
('89898989-2222-4222-8222-898989898989', 'Gavin', 'Campbell', '2626 Woodland Dr', 'Jacksonville', 'Florida', '32202', '5550506001', '5550506002', 'gavin.campbell@example.com', 'USER'),
('90909090-3333-4333-8333-909090909090', 'Hailey', 'Parker', '2727 Lake Shore Way', 'Albany', 'New York', '12207', '5550607001', '5550607002', 'hailey.parker@example.com', 'ADMIN'),
('01010101-4444-4444-8444-010101010101', 'Isaac', 'Edwards', '2828 Pine Valley Blvd', 'Little Rock', 'Arkansas', '72201', '5550708001', '5550708002', 'isaac.edwards@example.com', 'USER'),
('02020202-5555-4555-8555-020202020202', 'Jenna', 'Collins', '2929 Bayview Dr', 'Madison', 'Wisconsin', '53703', '5550809001', '5550809002', 'jenna.collins@example.com', 'USER'),
('03030303-6666-4666-8666-030303030303', 'Kyle', 'Stewart', '3030 Riverbend Rd', 'Tulsa', 'Oklahoma', '74103', '5550910001', '5550910002', 'kyle.stewart@example.com', 'ADMIN'),
('04040404-7777-4777-8777-040404040404', 'Lily', 'Sanchez', '3131 Prairie St', 'San Jose', 'California', '95113', '5551011001', '5551011002', 'lily.sanchez@example.com', 'USER'),
('05050505-8888-4888-8888-050505050505', 'Miles', 'Morris', '3232 Orchard Ave', 'Fargo', 'North Dakota', '58102', '5551112001', '5551112002', 'miles.morris@example.com', 'USER'),
('06060606-9999-4999-8999-060606060606', 'Nora', 'Rogers', '3333 Evergreen Ln', 'Omaha', 'Nebraska', '68102', '5551213001', '5551213002', 'nora.rogers@example.com', 'ADMIN'),
('07070707-aaaa-4aaa-8aaa-070707070707', 'Owen', 'Cook', '3434 Brookfield Dr', 'Newark', 'New Jersey', '07102', '5551314001', '5551314002', 'owen.cook@example.com', 'USER'),
('08080808-bbbb-4bbb-8bbb-080808080808', 'Piper', 'Murphy', '3535 Maple Ridge Rd', 'Anchorage', 'Alaska', '99501', '5551415001', '5551415002', 'piper.murphy@example.com', 'USER'),
('09090909-cccc-4ccc-8ccc-090909090909', 'Quinn', 'Bailey', '3636 Elmwood St', 'Trenton', 'New Jersey', '08608', '5551516001', '5551516002', 'quinn.bailey@example.com', 'ADMIN'),
('10101010-dddd-4ddd-8ddd-101010101010', 'Riley', 'Rivera', '3737 Meadowbrook Blvd', 'Providence', 'Rhode Island', '02903', '5551617001', '5551617002', 'riley.rivera@example.com', 'USER'),
('11121212-eeee-4eee-8eee-111212121212', 'Sophie', 'Reed', '3838 Glen Haven Rd', 'Baton Rouge', 'Louisiana', '70801', '5551718001', '5551718002', 'sophie.reed@example.com', 'USER'),
('12131313-ffff-4fff-8fff-121313131313', 'Trent', 'Kelly', '3939 Oakwood Ln', 'Tampa', 'Florida', '33602', '5551819001', '5551819002', 'trent.kelly@example.com', 'ADMIN'),
('13141414-1111-4111-8111-131414141414', 'Ursula', 'Howard', '4040 Redbud St', 'Hartford', 'Connecticut', '06103', '5551920001', '5551920002', 'ursula.howard@example.com', 'USER'),
('14151515-2222-4222-8222-141515151515', 'Vince', 'Ward', '4141 Timberland Dr', 'Wichita', 'Kansas', '67202', '5552021001', '5552021002', 'vince.ward@example.com', 'USER');


SELECT * FROM employees;
