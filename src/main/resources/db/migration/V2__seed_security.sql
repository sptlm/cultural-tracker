INSERT INTO roles (name)
VALUES ('ADMIN'), ('USER')
ON CONFLICT (name) DO NOTHING;

INSERT INTO users (username, email, password_hash)
VALUES ('admin', 'admin@cultural-navigator.local', '$2a$10$9pKxUkFKIBRJmXa26U/X0erFkNX4qbB3CImI4MlqRjcDH4eE0vqda')
ON CONFLICT (username) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = 'ADMIN'
WHERE u.username = 'admin'
ON CONFLICT DO NOTHING;
