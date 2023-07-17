INSERT INTO public.users (user_id, name, email)
    VALUES
(1,	'updateName',	'updateName@user.com'),
(4,	'user',	        'user@user.com'),
(5,	'other',	    'other@other.com'),
(6,	'practicum',	'practicum@yandex.ru');

INSERT INTO  public.item_request (item_request_id, description, requester_id, request_created)
    VALUES
(1,	'Хотел бы воспользоваться щёткой для обуви',	1,	'2023-07-14 14:05:04.531218');

INSERT INTO  public.item (item_id, name, description, available, owner, request)
    VALUES
(1,	'Аккумуляторная дрель',	'Аккумуляторная дрель + аккумулятор',	true,	1,	null),
(3,	'Клей Момент',	'Тюбик суперклея марки Момент',	true,	4,	null),
(2,	'Отвертка',	'Аккумуляторная отвертка',	true,	4,	null),
(4,	'Кухонный стол',	'Стол для празднования',	true,	6,	null),
(5,	'Щётка для обуви',	'Стандартная щётка для обуви',	true,	4,	1);

INSERT INTO  public.booking (booking_id, start_time, end_time, item_id, booker_id, status)
    VALUES
(1,	'2023-07-14 14:04:51',	'2023-07-14 14:04:52',	2,	1,	1),
(2,	'2023-07-15 14:04:48',	'2023-07-16 14:04:48',	2,	1,	1),
(3,	'2023-07-15 14:04:50',	'2023-07-15 15:04:50',	1,	4,	2),
(4,	'2023-07-14 15:04:50',	'2023-07-14 16:04:50',	2,	5,	1),
(5,	'2023-07-14 14:04:58',	'2023-07-15 14:04:55',	3,	1,	2),
(6,	'2023-07-14 14:04:58',	'2023-07-14 14:04:59',	2,	1,	1),
(8,	'2023-07-14 14:04:58',	'2023-07-14 15:04:56',  4,	1,	1),
(7,	'2023-07-24 14:04:56',	'2023-07-25 14:04:56',	1,	5,	1);

INSERT INTO  public.comments (comments_id, text, item_id, author_id, created)
    VALUES
(1,	'Add comment from user1',	2,	1,	'2023-07-14 14:05:02.937951');
