-- Example data of tbl_user.
INSERT INTO tbl_user (user_id, username, password, gender, fullname)
VALUES (1, 'anh', 123, '1', 'tuananh'),
  (2, 'ha', 123, '1', 'hongha'),
  (3, 'thien', 123, '1', 'thien')
ON CONFLICT (user_id) DO NOTHING;

-- Predefined roles
INSERT INTO user_role (id, role)
VALUES (1, 'admin'), (2, 'librarian'), (3, 'borrower')
ON CONFLICT (id) DO UPDATE SET role = EXCLUDED.role;
