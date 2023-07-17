DROP TABLE IF EXISTS public.booking;
DROP TABLE IF EXISTS public.comments;
DROP TABLE IF EXISTS public.item;
DROP TABLE IF EXISTS public.item_request;
DROP TABLE IF EXISTS public.users;

CREATE TABLE public.users (
                                        user_id bigserial    NOT NULL,
                                        name varchar(255) NOT NULL,
                                        email varchar(255) NOT NULL,
                                        CONSTRAINT User_pkey PRIMARY KEY (user_id),
                                        CONSTRAINT Email_Unique UNIQUE (email)
);

CREATE TABLE public.item_request (
                                        item_request_id bigserial NOT NULL,
                                        description varchar(255) NOT NULL,
                                        requester_id bigint NOT NULL,
                                        request_created timestamp without time zone NOT NULL,
                                        CONSTRAINT ItemRequest_pkey PRIMARY KEY (item_request_id),
                                        CONSTRAINT Requester_Id_FK FOREIGN KEY(requester_id) REFERENCES public.users (user_id)
                                            ON DELETE CASCADE
                                            ON UPDATE CASCADE
);

CREATE TABLE public.item (
                                        item_id bigserial NOT NULL,
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

CREATE TABLE public.booking (
                                        booking_id bigserial NOT NULL,
                                        start_time timestamp without time zone NOT NULL,
                                        end_time timestamp without time zone,
                                        item_id bigint NOT NULL,
                                        booker_id bigint NOT NULL,
                                        status integer NOT NULL,
                                        CONSTRAINT Booking_pkey PRIMARY KEY (booking_id),
                                        CONSTRAINT Booker_Id_FK FOREIGN KEY(booker_id)   REFERENCES public.users (user_id)
                                            ON DELETE CASCADE
                                            ON UPDATE CASCADE,
                                        CONSTRAINT Item_Id_FK FOREIGN KEY(item_id)   REFERENCES public.item (item_id)
                                            ON DELETE CASCADE
                                            ON UPDATE CASCADE
);

CREATE TABLE public.comments (
                                        comments_id bigserial NOT NULL,
                                        text varchar(255) NOT NULL,
                                        item_id bigint NOT NULL,
                                        author_id bigint NOT NULL,
                                        created timestamp without time zone NOT NULL,
                                        CONSTRAINT Comments_pkey PRIMARY KEY (comments_id),
                                        CONSTRAINT Author_Id_FK FOREIGN KEY(author_id)   REFERENCES public.users (user_id)
                                            ON DELETE CASCADE
                                            ON UPDATE CASCADE,
                                        CONSTRAINT Item_Id_FK FOREIGN KEY(item_id)   REFERENCES public.item (item_id)
                                            ON DELETE CASCADE
                                            ON UPDATE CASCADE
);
