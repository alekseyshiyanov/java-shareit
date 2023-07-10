

CREATE TABLE IF NOT EXISTS public.users (
                                             user_id bigserial    NOT NULL,
                                             name varchar(255) NOT NULL,
                                             email varchar(255) NOT NULL,
                                             CONSTRAINT User_pkey PRIMARY KEY (user_id),
                                             CONSTRAINT Email_Unique UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS public.item_request (
                                                  item_request_id bigint NOT NULL,
                                                  description varchar(255) NOT NULL,
                                                  requester_id bigint NOT NULL,
                                                  request_created time(6) without time zone NOT NULL,
                                                  CONSTRAINT ItemRequest_pkey PRIMARY KEY (item_request_id),
                                                  FOREIGN KEY(Requester_Id)   REFERENCES public.users (user_id)
                                                      ON DELETE CASCADE
                                                      ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS public.item (
                                           item_id bigserial NOT NULL,
                                           name varchar(255) NOT NULL,
                                           description varchar(255) NOT NULL,
                                           available boolean NOT NULL,
                                           owner bigint NOT NULL,
                                           request bigint,
                                           CONSTRAINT Item_pkey PRIMARY KEY (item_id),
                                           FOREIGN KEY(Owner)   REFERENCES public.users (user_id)
                                           ON DELETE CASCADE
                                               ON UPDATE CASCADE,
                                           FOREIGN KEY(Request)   REFERENCES public.item_request (item_request_id)
                                               ON DELETE CASCADE
                                               ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS public.booking (
                                                booking_id bigserial NOT NULL,
                                                start_time timestamp without time zone NOT NULL,
                                                end_time timestamp without time zone,
                                                item_id bigint NOT NULL,
                                                booker_id bigint NOT NULL,
                                                status integer NOT NULL,
                                                CONSTRAINT Booking_pkey PRIMARY KEY (Booking_Id),
                                                FOREIGN KEY(Booker_Id)   REFERENCES public.users (user_id)
                                                ON DELETE CASCADE
                                                    ON UPDATE CASCADE,
                                                FOREIGN KEY(item_id)   REFERENCES public.item (item_id)
                                                    ON DELETE CASCADE
                                                    ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS public.comments (
                                                comments_id bigserial NOT NULL,
                                                text varchar(255) NOT NULL,
                                                item_id bigint NOT NULL,
                                                author_id bigint NOT NULL,
                                                created timestamp without time zone NOT NULL,
                                                CONSTRAINT Comments_pkey PRIMARY KEY (comments_id),
                                                FOREIGN KEY(Author_Id)   REFERENCES public.users (user_id)
                                                  ON DELETE CASCADE
                                                  ON UPDATE CASCADE,
                                                FOREIGN KEY(Item_Id)   REFERENCES public.item (item_id)
                                                  ON DELETE CASCADE
                                                  ON UPDATE CASCADE
);
