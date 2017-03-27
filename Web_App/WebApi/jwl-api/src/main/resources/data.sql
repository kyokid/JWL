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
VALUES  ('SE61476', '123', '3', FALSE, TRUE);
INSERT INTO account (user_id, password, role_id, is_in_library, is_activated)
VALUES  ('thiendn', '123', '3', FALSE, TRUE),
  ('havh', '123', '3', FALSE, TRUE);
INSERT INTO account (user_id, password, role_id, is_in_library, is_activated)
VALUES  ('admin', 'admin123', '1', FALSE, TRUE),
  ('librarian', 'librarian123', '2', FALSE, TRUE);

INSERT INTO borrower_ticket (qr_id, user_id, create_date)
VALUES  ('123', 'SE61476', '2017-01-01');
UPDATE borrower_ticket SET delete_date = NULL WHERE qr_id = '123';

INSERT INTO author (id, name, description)
VALUES (1, 'Bert Bates', ''), (2, 'Kathy Sierra', ''), (3, 'Michael Hartl', '');

INSERT INTO book_author (id, author_id, book_id)
VALUES (1, 1, 1), (2, 2, 3), (3, 3, 2), (4, 2, 4), (5, 1, 5), (6, 3, 5);

INSERT INTO category (id, name, description)
VALUES (1, 'math', ''), (2, 'literature', ''), (3, 'physics', ''), (4, 'biography', ''), (5, 'IT', '');
INSERT INTO category (id, name, description)
VALUES (6, 'business', ''), (7, 'Ruby', ''), (8, 'Java', ''), (9, 'Xml', ''), (10, 'Data', '');

INSERT INTO book_category (id, book_id, category_id)
VALUES (1, 1, 5), (2, 1, 8), (3, 2, 5), (4, 2, 7), (5, 3, 5), (6, 3, 9), (7, 4, 5), (8, 5, 6);

INSERT INTO book_type (id, name, borrow_limit_days, days_per_extend, extend_times_limit)
VALUES (1, 'Reference', 5, 3, 3), (2, 'textbook', 90, 7, 3);

INSERT INTO book_position (id, shelf, floor)
VALUES (1, 'A', 'ground'), (2, 'B', 'ground'), (3, 'A', '1st floor'), (4, 'B', '1st floor');

INSERT INTO book (id, title, publisher, description, publish_year, number_of_pages, book_type_id, position_id, isbn)
VALUES (1, 'Java', 'Zert', 'The beginning part to greatness.', '2015', '400', 2, 1, 'JV1'),
  (2, 'Ruby on Rails', 'Rails', 'The quickest way to web development.', '2015', '200', 1, 2, 'RoR2');
INSERT INTO book (id, title, publisher, description, publish_year, number_of_pages, book_type_id, position_id, isbn)
VALUES (3, 'XML', 'KhanhKT', 'How to survive with XML', '2017', '230', 2, 1, 'XML3');
INSERT INTO book (id, title, publisher, description, publish_year, number_of_pages, book_type_id, position_id, isbn)
VALUES (4, 'Clean Architecture', 'Clean Code Publisher', 'A must have to build a clean project.', '2017', '400', 1, 4, 'CA4'),
  (5, 'Business English', 'Business Co.', 'English for your business.', '2016', '800', 2, 3, 'BE4');
UPDATE book
SET number_of_copies = CASE id
            WHEN 1 THEN 4
            WHEN 2 THEN 3
            WHEN 3 THEN 1
            WHEN 4 THEN 2
            WHEN 5 THEN 1
            END
WHERE id IN (1, 2, 3, 4, 5);
UPDATE book
SET price = CASE id
            WHEN 1 THEN 10
            WHEN 2 THEN 20
            WHEN 3 THEN 30
            WHEN 4 THEN 40
            WHEN 5 THEN 50
            END
WHERE id IN (1, 2, 3, 4, 5);
UPDATE book
SET thumbnail = CASE id
                WHEN 1 THEN 'http://se.uploads.ru/VijQo.jpg'
                WHEN 2 THEN 'http://sg.uploads.ru/s7AIO.png'
                WHEN 3 THEN 'http://sh.uploads.ru/C9Hyf.jpg'
                WHEN 4 THEN 'http://s0.uploads.ru/JoEBI.jpg'
                WHEN 5 THEN 'http://s1.uploads.ru/gB4mi.jpg'
                END
WHERE id IN (1, 2, 3, 4, 5);

INSERT INTO book_copy (rfid, book_id, price)
VALUES ('0009599367', 1, 400), ('0009951006', 1, 350), ('0010323151', 2, 100), ('0010315397', 2, 100);
UPDATE book_copy SET book_id = 3 WHERE rfid = '0010315397';
INSERT INTO book_copy (rfid, book_id, price)
VALUES ('0001182226', 4, 800), ('0001190428', 5, 750), ('0001122520', 4, 800);

INSERT INTO profile (user_id, fullname, email, address, date_of_birth, phone_no, place_of_work)
VALUES ('SE61476', 'Nguyễn Tuấn Anh', 'dratannta@gmail.com', '433 Tân Sơn F12 Gò Vấp', '1992-04-26', '01692536559', 'FPT');
INSERT INTO profile (user_id, fullname, email, address, date_of_birth, phone_no, place_of_work)
VALUES ('thiendn', 'Đặng Nhật Thiên', 'thiendn@gmail.com', 'Đối diện trường', '1994-02-28', '01678785551', 'FPT'),
  ('havh', 'Võ Hồng Hà', 'havh0108@gmail.com', '23 TCH 35 Q12', '1994-06-28', '01635782661', 'FPT');
INSERT INTO profile (user_id, fullname, email, address, date_of_birth, phone_no, place_of_work)
VALUES ('admin', 'Nguyễn Tuấn Anh', 'dratannta@gmail.com', '433 Tân Sơn F12 Gò Vấp', '1992-04-26', '01692536559', 'FPT'),
  ('librarian', 'Nguyễn Tuấn Anh', 'dratannta@gmail.com', '433 Tân Sơn F12 Gò Vấp', '1992-04-26', '01692536559', 'FPT');
UPDATE profile
SET gender = CASE user_id
             WHEN 'SE61476' THEN 'male'
             WHEN 'thiendn' THEN 'female'
             WHEN 'havh' THEN 'male'
             END
WHERE user_id IN ('SE61476', 'thiendn', 'havh');
