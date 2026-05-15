INSERT INTO roles (name) VALUES ('ADMIN'), ('USER');

INSERT INTO users (username, email, password_hash, preferred_budget, preferred_address, preferred_latitude, preferred_longitude) VALUES
    ('admin', 'admin@cultural-navigator.local', '$2a$10$9pKxUkFKIBRJmXa26U/X0erFkNX4qbB3CImI4MlqRjcDH4eE0vqda', 2000, 'Казань, Кремлевская, 18', 55.7965, 49.1088),
    ('Айсиписяев', 'aisipisyaev@cultural-navigator.local', '$2a$10$9pKxUkFKIBRJmXa26U/X0erFkNX4qbB3CImI4MlqRjcDH4eE0vqda', 1200, 'Казань, Пушкина, 86', 55.7906, 49.1343),
    ('арбуз', 'arbuz@cultural-navigator.local', '$2a$10$9pKxUkFKIBRJmXa26U/X0erFkNX4qbB3CImI4MlqRjcDH4eE0vqda', 0, 'Казань, Декабристов, 2', 55.8182, 49.1025),
    ('юджин', 'eugene@cultural-navigator.local', '$2a$10$9pKxUkFKIBRJmXa26U/X0erFkNX4qbB3CImI4MlqRjcDH4eE0vqda', 1800, 'Казань, Петербургская, 57', 55.7815, 49.1352),
    ('спирт', 'spirit@cultural-navigator.local', '$2a$10$9pKxUkFKIBRJmXa26U/X0erFkNX4qbB3CImI4MlqRjcDH4eE0vqda', 700, 'Казань, Баумана, 51', 55.7908, 49.1156),
    ('абсолут', 'absolut@cultural-navigator.local', '$2a$10$9pKxUkFKIBRJmXa26U/X0erFkNX4qbB3CImI4MlqRjcDH4eE0vqda', 3000, 'Казань, Сибгата Хакима, 4', 55.8157, 49.1374),
    ('мишель', 'michelle@cultural-navigator.local', '$2a$10$9pKxUkFKIBRJmXa26U/X0erFkNX4qbB3CImI4MlqRjcDH4eE0vqda', 1500, 'Казань, Островского, 10', 55.7861, 49.1192);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'ADMIN' WHERE u.username = 'admin';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = 'USER'
WHERE u.username <> 'admin';
