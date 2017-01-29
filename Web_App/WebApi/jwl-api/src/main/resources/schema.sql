
-- user_role:
-- Store all role of the system.
CREATE TABLE IF NOT EXISTS public.user_role
(
  id SERIAL PRIMARY KEY,
  role text NOT NULL
);

-- example purpose only.
CREATE TABLE IF NOT EXISTS tbl_user
(
  user_id INTEGER PRIMARY KEY NOT NULL,
  username TEXT,
  password TEXT,
  gender BIT,
  fullname TEXT
);


-- account:
-- user_id, password, and other frequent access attributes.
CREATE TABLE IF NOT EXISTS public.account
(
  user_id text NOT NULL,
  password text NOT NULL,
  role_id integer NOT NULL,
  is_in_library boolean NOT NULL DEFAULT false,
  is_activated boolean NOT NULL DEFAULT false,
  CONSTRAINT account_pkey PRIMARY KEY (user_id),
  CONSTRAINT account_user_role_id_fk FOREIGN KEY (role_id)
  REFERENCES public.user_role (id) MATCH SIMPLE
  ON UPDATE CASCADE ON DELETE NO ACTION
);

-- profile:
-- Other information of a user.
CREATE TABLE IF NOT EXISTS public.profile
(
  user_id text NOT NULL,
  fullname text NOT NULL,
  email text,
  address text NOT NULL,
  date_of_birth date NOT NULL,
  phone_no text NOT NULL,
  place_of_work text NOT NULL,
  CONSTRAINT profile_pkey PRIMARY KEY (user_id),
  CONSTRAINT profile_account_user_id_fk FOREIGN KEY (user_id)
  REFERENCES public.account (user_id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);


CREATE UNIQUE INDEX IF NOT EXISTS user_role_role_uindex
  ON public.user_role
  USING btree
  (role COLLATE pg_catalog."default");

-- author:
-- Writer of books.
CREATE TABLE IF NOT EXISTS public.author
(
  id SERIAL PRIMARY KEY,
  name text NOT NULL,
  description text
);

-- category:
-- Categories of books: science, literature, art, ...
CREATE TABLE IF NOT EXISTS public.category
(
  id SERIAL PRIMARY KEY,
  name text NOT NULL,
  description text
);
CREATE UNIQUE INDEX IF NOT EXISTS category_name_uindex
  ON public.category
  USING btree
  (name COLLATE pg_catalog."default");


-- book_position:
-- Position of a book: its shelf, floor.
CREATE TABLE IF NOT EXISTS public.book_position
(
  id SERIAL PRIMARY KEY,
  shelf text NOT NULL,
  floor text NOT NULL
);

-- book_type:
-- Each type comes with a specific number of borrow limit days.
CREATE TABLE IF NOT EXISTS public.book_type
(
  id SERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  borrow_limit_days INT NOT NULL,
  days_per_extend INT NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS book_type_name_uindex ON public.book_type (name);
-- book:
-- Hold all information of a book, and the number of its copies.
CREATE TABLE IF NOT EXISTS public.book
(
  id SERIAL PRIMARY KEY,
  title text NOT NULL,
  publisher text NOT NULL,
  description text,
  publish_year integer NOT NULL,
  number_of_pages integer NOT NULL,
  number_of_copies integer NOT NULL DEFAULT 0,
  book_type_id integer NOT NULL,
  position_id integer,
  isbn text,
  CONSTRAINT book_book_position_id_fk FOREIGN KEY (position_id)
  REFERENCES public.book_position (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT book_book_type_id_fk FOREIGN KEY (book_type_id)
  REFERENCES public.book_type (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);


-- book_category:
-- Relation table of book and category.
CREATE TABLE IF NOT EXISTS public.book_category
(
  book_id integer NOT NULL,
  category_id integer NOT NULL,
  CONSTRAINT book_category_pkey PRIMARY KEY (category_id, book_id),
  CONSTRAINT book_category_book_id_fk FOREIGN KEY (book_id)
  REFERENCES public.book (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT book_category_category_id_fk FOREIGN KEY (category_id)
  REFERENCES public.category (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);

-- book_author:
-- Relation table of book and author.
CREATE TABLE IF NOT EXISTS public.book_author
(
  book_id integer NOT NULL,
  author_id integer NOT NULL,
  CONSTRAINT book_author_pkey PRIMARY KEY (author_id, book_id),
  CONSTRAINT book_author_author_id_fk FOREIGN KEY (author_id)
  REFERENCES public.author (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT book_author_book_id_fk FOREIGN KEY (book_id)
  REFERENCES public.book (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);



-- wish_book:
-- book in a wish list of a user.
CREATE TABLE IF NOT EXISTS public.wish_book
(
  book_id integer NOT NULL,
  user_id text NOT NULL,
  CONSTRAINT wish_book_user_id_book_id_pk PRIMARY KEY (user_id, book_id),
  CONSTRAINT wish_book_account_user_id_fk FOREIGN KEY (user_id)
  REFERENCES public.account (user_id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT wish_book_book_id_fk FOREIGN KEY (book_id)
  REFERENCES public.book (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);

-- book_copy:
-- A copy of a book.
CREATE TABLE IF NOT EXISTS public.book_copy
(
  rfid text NOT NULL,
  book_id integer NOT NULL,
  price integer NOT NULL,
  CONSTRAINT book_copy_pkey PRIMARY KEY (rfid),
  CONSTRAINT book_copy_book_id_fk FOREIGN KEY (book_id)
  REFERENCES public.book (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);

-- borrowed_book_copy:
-- A copy that is being borrowed.
CREATE TABLE IF NOT EXISTS public.borrowed_book_copy
(
  id SERIAL PRIMARY KEY,
  book_copy_id TEXT NOT NULL,
  user_id TEXT NOT NULL,
  borrow_limit_days INT NOT NULL,
  extend_times INT NOT NULL,
  extend_times_limit INT NOT NULL,
  days_per_extend INT NOT NULL,
  borrowed_date DATE,
  return_date DATE,
  CONSTRAINT borrowed_book_copy_book_copy_rfid_fk FOREIGN KEY (book_copy_id) REFERENCES book_copy (rfid),
  CONSTRAINT borrowed_book_copy_account_user_id_fk FOREIGN KEY (user_id) REFERENCES account (user_id)
);

-- borrower_ticket:
-- A ticket that a borrower uses to check in library.
CREATE TABLE IF NOT EXISTS public.borrower_ticket
(
  qr_id TEXT PRIMARY KEY,
  user_id TEXT NOT NULL,
  create_date DATE NOT NULL,
  scan_date DATE,
  delete_date DATE,
  CONSTRAINT borrower_ticket_account_user_id_fk FOREIGN KEY (user_id) REFERENCES account (user_id)
);
