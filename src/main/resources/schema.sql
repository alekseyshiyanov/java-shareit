

CREATE TABLE IF NOT EXISTS public.Users (
                                             User_Id bigserial    NOT NULL,
                                             Name varchar(255) NOT NULL,
                                             Email varchar(255) NOT NULL,
                                             CONSTRAINT User_pkey PRIMARY KEY (User_Id),
                                             CONSTRAINT Email_Unique UNIQUE (Email)
);

CREATE TABLE IF NOT EXISTS public.ItemRequest (
                                                  ItemRequest_Id bigint NOT NULL,
                                                  Description varchar(255) NOT NULL,
                                                  Requester_Id bigint NOT NULL,
                                                  Request_Created time(6) without time zone NOT NULL,
                                                  CONSTRAINT ItemRequest_pkey PRIMARY KEY (ItemRequest_Id),
                                                  FOREIGN KEY(Requester_Id)   REFERENCES public.Users (User_Id)
                                                      ON DELETE CASCADE
                                                      ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS public.Item (
                                           Item_Id bigserial NOT NULL,
                                           Name varchar(255) NOT NULL,
                                           Description varchar(255) NOT NULL,
                                           Available boolean NOT NULL,
                                           Owner bigint NOT NULL,
                                           Request bigint,
                                           CONSTRAINT Item_pkey PRIMARY KEY (Item_Id),
                                           FOREIGN KEY(Owner)   REFERENCES public.Users (User_Id)
                                           ON DELETE CASCADE
                                               ON UPDATE CASCADE,
                                           FOREIGN KEY(Request)   REFERENCES public.ItemRequest (ItemRequest_Id)
                                               ON DELETE CASCADE
                                               ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS public.Booking (
                                                Booking_Id bigserial NOT NULL,
                                                Start_Time timestamp without time zone NOT NULL,
                                                End_Time timestamp without time zone,
                                                Item_Id bigint NOT NULL,
                                                Booker_Id bigint NOT NULL,
                                                Status integer NOT NULL,
                                                CONSTRAINT Booking_pkey PRIMARY KEY (Booking_Id),
                                                FOREIGN KEY(Booker_Id)   REFERENCES public.Users (User_Id)
                                                ON DELETE CASCADE
                                                    ON UPDATE CASCADE,
                                                FOREIGN KEY(Item_Id)   REFERENCES public.Item (Item_Id)
                                                    ON DELETE CASCADE
                                                    ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS public.Comments (
                                                Comments_Id bigserial NOT NULL,
                                                Text varchar(255) NOT NULL,
                                                Item_Id bigint NOT NULL,
                                                Author_Id bigint NOT NULL,
                                                Created timestamp without time zone NOT NULL,
                                                CONSTRAINT Comments_pkey PRIMARY KEY (Comments_Id),
                                                FOREIGN KEY(Author_Id)   REFERENCES public.Users (User_Id)
                                                  ON DELETE CASCADE
                                                  ON UPDATE CASCADE,
                                                FOREIGN KEY(Item_Id)   REFERENCES public.Item (Item_Id)
                                                  ON DELETE CASCADE
                                                  ON UPDATE CASCADE
);
