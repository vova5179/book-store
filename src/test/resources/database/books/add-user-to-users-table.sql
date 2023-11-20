INSERT INTO users (id, email, password, first_name, last_name, shipping_address, is_deleted)
VALUES (1 ,'admin@example.com', 'password123', 'John', 'Doe', '123 Main St, Country', false);

INSERT INTO users_roles (user_id, role_id)
VALUES (1, 2);

