-- DROP TABLE public.comments;
-- DROP TABLE public.booking;
-- DROP TABLE public.item;
-- DROP TABLE public.item_request;
-- DROP TABLE public.users;

CREATE TABLE IF NOT EXISTS public.users (
                                        user_id integer auto_increment    NOT NULL,
                                        name varchar(255) NOT NULL,
                                        email varchar(255) NOT NULL,
                                        CONSTRAINT User_pkey PRIMARY KEY (user_id),
                                        CONSTRAINT Email_Unique UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS public.item_request (
                                        item_request_id integer auto_increment NOT NULL,
                                        description varchar(255) NOT NULL,
                                        requester_id bigint NOT NULL,
                                        request_created timestamp without time zone NOT NULL,
                                        CONSTRAINT ItemRequest_pkey PRIMARY KEY (item_request_id),
                                        CONSTRAINT Requester_Id_FK FOREIGN KEY(requester_id) REFERENCES public.users (user_id)
                                            ON DELETE CASCADE
                                            ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS public.item (
                                        item_id integer auto_increment NOT NULL,
                                        name varchar(255) NOT NULL,
                                        description varchar(255) NOT NULL,
                                        available boolean NOT NULL,
                                        owner bigint NOT NULL,
                                        request bigint,
                                        CONSTRAINT Item_pkey PRIMARY KEY (item_id),
                                        CONSTRAINT Owner_FK FOREIGN KEY(owner) REFERENCES public.users (user_id)
                                            ON DELETE CASCADE
                                            ON UPDATE CASCADE,
                                        CONSTRAINT Request_FK FOREIGN KEY(request) REFERENCES public.item_request (item_request_id)
                                            ON DELETE CASCADE
                                            ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS public.booking (
                                        booking_id integer auto_increment NOT NULL,
                                        start_time timestamp without time zone NOT NULL,
                                        end_time timestamp without time zone,
                                        item_id bigint NOT NULL,
                                        booker_id bigint NOT NULL,
                                        status integer NOT NULL,
                                        CONSTRAINT Booking_pkey PRIMARY KEY (booking_id),
                                        CONSTRAINT Booker_Id_FK FOREIGN KEY(booker_id)   REFERENCES public.users (user_id)
                                            ON DELETE CASCADE
                                            ON UPDATE CASCADE,
                                        CONSTRAINT Booking_Item_Id_FK FOREIGN KEY(item_id)   REFERENCES public.item (item_id)
                                            ON DELETE CASCADE
                                            ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS public.comments (
                                        comments_id integer auto_increment NOT NULL,
                                        text varchar(255) NOT NULL,
                                        item_id bigint NOT NULL,
                                        author_id bigint NOT NULL,
                                        created timestamp without time zone NOT NULL,
                                        CONSTRAINT Comments_pkey PRIMARY KEY (comments_id),
                                        CONSTRAINT Author_Id_FK FOREIGN KEY(author_id)   REFERENCES public.users (user_id)
                                            ON DELETE CASCADE
                                            ON UPDATE CASCADE,
                                        CONSTRAINT Comments_Item_Id_FK FOREIGN KEY(item_id)   REFERENCES public.item (item_id)
                                            ON DELETE CASCADE
                                            ON UPDATE CASCADE
);
