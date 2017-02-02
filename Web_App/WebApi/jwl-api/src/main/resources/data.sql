-- Example data of tbl_user.
INSERT INTO tbl_user (user_id, username, password, gender, fullname)
VALUES (1, 'anh', 123, '1', 'tuananh'),
  (2, 'ha', 123, '1', 'hongha'),
  (3, 'thien', 123, '1', 'thien');
-- ON CONFLICT (user_id) DO NOTHING;

-- Predefined roles
INSERT INTO user_role (id, role)
VALUES (1, 'admin'), (2, 'librarian'), (3, 'borrower');
-- ON CONFLICT (id) DO UPDATE SET role = EXCLUDED.role;

INSERT INTO account (user_id, password, role_id, is_in_library, is_activated)
VALUES  ('SE61476', '123', '3', TRUE, TRUE);

INSERT INTO borrower_ticket (qr_id, user_id, create_date)
VALUES  ('123', 'SE61476', '2017-01-01');
UPDATE borrower_ticket SET delete_date = NULL WHERE qr_id = '123';

INSERT INTO author (id, name, description)
VALUES (1, 'Bert Bates', ''), (2, 'Kathy Sierra', ''), (3, 'Michael Hartl', '');

INSERT INTO category (id, name, description)
VALUES (1, 'math', ''), (2, 'literature', ''), (3, 'physics', ''), (4, 'biography', ''), (5, 'IT', '');

INSERT INTO book_type (id, name, borrow_limit_days, days_per_extend, extend_times_limit)
VALUES (1, 'Reference', 5, 3, 3), (2, 'textbook', 90, 7, 3);

INSERT INTO book_position (id, shelf, floor)
VALUES (1, 'A', 'ground'), (2, 'B', 'ground'), (3, 'A', '1st floor'), (4, 'B', '1st floor');

INSERT INTO book (id, title, publisher, description, publish_year, number_of_pages, book_type_id, position_id, isbn)
VALUES (1, 'Java', 'Zert', 'The beginning part to greatness.', '2015', '400', 2, 1, ''),
  (2, 'Ruby on Rails', 'Rails', 'The quickest way to web development.', '2015', '200', 1, 2, '');

INSERT INTO book_copy (rfid, book_id, price)
VALUES ('J1', 1, 400), ('J2', 1, 350), ('R1', 2, 100), ('R2', 2, 100);
